/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.cluster.loadbalance;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.support.RpcUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.dubbo.common.constants.CommonConstants.COMMA_SPLIT_PATTERN;

/**
 * ConsistentHashLoadBalance
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "consistenthash";

    /**
     * Hash nodes name
     */
    public static final String HASH_NODES = "hash.nodes";

    /**
     * Hash arguments name
     */
    public static final String HASH_ARGUMENTS = "hash.arguments";

    private final ConcurrentMap<String, ConsistentHashSelector<?>> selectors = new ConcurrentHashMap<String, ConsistentHashSelector<?>>();

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // 获取到当前RPC调用的简单方法名
        String methodName = RpcUtils.getMethodName(invocation);
        // 获致到RPC调用的全限定性方法名
        String key = invokers.get(0).getUrl().getServiceKey() + "." + methodName;
        // 获取到invokers的hash，其将来用于构建selector
        int identityHashCode = System.identityHashCode(invokers);
        // 从缓存中获取当前方法的选择器selector
        ConsistentHashSelector<T> selector = (ConsistentHashSelector<T>) selectors.get(key);
        // 若selector为null，或这个存在的selector的hash与invokers的hash不同，则创建一个新的selector并放入到缓存
        if (selector == null || selector.identityHashCode != identityHashCode) {
            // 创建一个新的selector
            selectors.put(key, new ConsistentHashSelector<T>(invokers, methodName, identityHashCode));
            selector = (ConsistentHashSelector<T>) selectors.get(key);
        }
        // 使用selector根据调用信息进行选择
        return selector.select(invocation);
    }

    private static final class ConsistentHashSelector<T> {

        private final TreeMap<Long, Invoker<T>> virtualInvokers;

        private final int replicaNumber;

        private final int identityHashCode;

        private final int[] argumentIndex;

        ConsistentHashSelector(List<Invoker<T>> invokers, String methodName, int identityHashCode) {
            // 创建一个map用于存放虚拟invoker，key为虚拟invoker(其本质就是一个hash值)，value为物理invoker
            this.virtualInvokers = new TreeMap<Long, Invoker<T>>();
            this.identityHashCode = identityHashCode;
            URL url = invokers.get(0).getUrl();
            // 获取hash.nodes属性值，即为每个物理invoker创建的虚拟invoker的数量，默认160
            this.replicaNumber = url.getMethodParameter(methodName, HASH_NODES, 160);
            // 获取hash.arguments属性值，并使用逗号(,)进行分隔
            String[] index = COMMA_SPLIT_PATTERN.split(url.getMethodParameter(methodName, HASH_ARGUMENTS, "0"));
            // 这个数组中的元素将来是hash.arguments属性值解析出的索引转换为整型后的数值
            argumentIndex = new int[index.length];
            for (int i = 0; i < index.length; i++) {
                argumentIndex[i] = Integer.parseInt(index[i]); // 将String变为Integer写入到数组
            }
            // 遍历所有物理invoker，为每一个物理invoker创建replicaNumber个虚拟invoker
            for (Invoker<T> invoker : invokers) {
                // 获取物理invoker的地址  ip:port
                String address = invoker.getUrl().getAddress();
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 使用md5算法生成一个128位的摘要: 生成一个hash值需要使用一个32位的二进制数，
                    // 所以一个digest可以生成4个hash，将这个digest分为4段：0-31, 32-63, 64-95, 96-127
                    byte[] digest = md5(address + i);
                    // 使用digest的每一段生成一个hash
                    for (int h = 0; h < 4; h++) {
                        // 使用一个32位的二进制数生成一个hash，这个hash就代表了一个虚拟invoker
                        long m = hash(digest, h);
                        // 将这个hash(虚拟invoker)作为key，物理invoker作为value，写入到map
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        public Invoker<T> select(Invocation invocation) {
            // 将指定的实参值进行字符串拼接
            String key = toKey(invocation.getArguments());
            // 根据这个key计算出摘要
            byte[] digest = md5(key);
            // 使用这个digest的前32位(0-31)计算出一个hash，然后使用这个hash做选择
            return selectForKey(hash(digest, 0));
        }

        private String toKey(Object[] args) {
            StringBuilder buf = new StringBuilder();
            // 遍历argumentIndex的每一个元素，这些元素是RPC调用方法的实参的索引
            for (int i : argumentIndex) {
                // 若这个遍历的索引值在有效范围内，则将这个实参拼接
                if (i >= 0 && i < args.length) {
                    buf.append(args[i]);
                }
            }
            return buf.toString();
        }

        private Invoker<T> selectForKey(long hash) {
            // 选择出一个虚拟invoker的entry
            // 选择比当前指定hash值大的最小的虚拟invoker(hash值)，若不存在，则返回null
            Map.Entry<Long, Invoker<T>> entry = virtualInvokers.ceilingEntry(hash);
            // 若这个entry为null，则选择第一个虚拟invoker的entry
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            // 返回选择的虚拟invoker对应的物理invoker
            return entry.getValue();
        }

        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        private byte[] md5(String value) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            md5.reset();
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            md5.update(bytes);
            return md5.digest();
        }

    }

}
