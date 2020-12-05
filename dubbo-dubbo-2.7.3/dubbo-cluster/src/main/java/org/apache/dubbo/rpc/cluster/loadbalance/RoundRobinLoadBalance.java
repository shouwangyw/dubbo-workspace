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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Round robin load balance.
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "roundrobin";

    // 回收期，默认60秒
    // 若某个invoker的轮询权重当前值“一直”没有变化，
    // 则说明每次轮询时都没有其对应的这个invoker，
    // 说明这个invoker挂了，需要将其从缓存map中清除。
    // 这个“一直”就是这里的 回收期
    private static final int RECYCLE_PERIOD = 60000;

    // 轮询权重
    protected static class WeightedRoundRobin {
        // 主机权重
        private int weight;
        // 轮询权重当前值
        private AtomicLong current = new AtomicLong(0);
        // 记录轮询权重当前值的增重时间戳
        private long lastUpdate;
        // 获取主机权重
        public int getWeight() {
            return weight;
        }
        // 初始化主机权重
        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);  // 将轮询权重当前值归0
        }
        // 轮询权重当前值增重，每次增重一个主机权重
        public long increaseCurrent() {
            return current.addAndGet(weight);
        }
        // 轮询权重当前值减重，每次增重一个所有主机权重之和
        public void sel(int total) {
            current.addAndGet(-1 * total);
        }
        // 获取最后的增重时间戳
        public long getLastUpdate() {
            return lastUpdate;
        }
        // 更新最后的增重时间戳
        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }

    // 这是一个双层map，
    // 外层map的key是全限定性方法名，value是内层map
    // 内层map的key为一个invoker的url，其就代表了一个invoker，所以我们就可以将其直接视为invoker
    // 内层map的value为轮询权重实例
    // 这个内层map与其对应的外层map的key间的关系是，内层map可以提供外层map的key(方法)的服务
    private ConcurrentMap<String, ConcurrentMap<String, WeightedRoundRobin>> methodWeightMap = new ConcurrentHashMap<String, ConcurrentMap<String, WeightedRoundRobin>>();
    // 更新锁，其值为false，表示锁是打开的
    private AtomicBoolean updateLock = new AtomicBoolean();
    
    /**
     * get invoker addr list cached for specified invocation
     * <p>
     * <b>for unit test only</b>
     * 
     * @param invokers
     * @param invocation
     * @return
     */
    protected <T> Collection<String> getInvokerAddrList(List<Invoker<T>> invokers, Invocation invocation) {
        String key = invokers.get(0).getUrl().getServiceKey() + "." + invocation.getMethodName();
        Map<String, WeightedRoundRobin> map = methodWeightMap.get(key);
        if (map != null) {
            return map.keySet();
        }
        return null;
    }
    
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // 获取当前RPC调用的方法的全限定性方法名
        String key = invokers.get(0).getUrl().getServiceKey() + "." + invocation.getMethodName();
        // 从缓存Map中获取当前调用方法所对应的所有invoker及其对应的轮询权重，即内层map
        ConcurrentMap<String, WeightedRoundRobin> map = methodWeightMap.get(key);
        // 若这个内层map为null，说明要么没有这个方法，要么真的是这个map为null
        // 此时创建一个内层map放入到缓存Map中
        if (map == null) {
            methodWeightMap.putIfAbsent(key, new ConcurrentHashMap<String, WeightedRoundRobin>());
            map = methodWeightMap.get(key);
        }
        int totalWeight = 0;
        // 记录本轮循环中轮询权重当前值的最大值
        long maxCurrent = Long.MIN_VALUE;
        // 当前时间
        long now = System.currentTimeMillis();
        // 记录最终被选择出的invoker及其对应的轮询权重实例
        Invoker<T> selectedInvoker = null;
        WeightedRoundRobin selectedWRR = null;

        // 遍历所有invoker
        for (Invoker<T> invoker : invokers) {
            // 获取当前遍历invoker的url，即内层map的key，格式  dubbo://ip:port/业务接口名
            String identifyString = invoker.getUrl().toIdentityString();
            // 从缓存中获取当前invoker对应的轮询权重实例
            WeightedRoundRobin weightedRoundRobin = map.get(identifyString);
            // 获取当前invoker的主机权重
            int weight = getWeight(invoker, invocation);
            // 若当前invoker对应的轮询权重实例为null，则创建一个新的，初始化后再写入到缓存map
            if (weightedRoundRobin == null) {
                weightedRoundRobin = new WeightedRoundRobin();
                weightedRoundRobin.setWeight(weight);
                map.putIfAbsent(identifyString, weightedRoundRobin);
            }
            // 什么时候主机权重会发生变化？warmup过程中的主机权重
            // 会随着时间的推移，越来越接近weight设置值
            if (weight != weightedRoundRobin.getWeight()) {
                //weight changed
                // 更新当前轮询权重实例的主机权重。注意，此时也会将轮询权重当前值归0
                weightedRoundRobin.setWeight(weight);
            }
            // 为当前invoker的轮询权重当前值增重
            long cur = weightedRoundRobin.increaseCurrent();
            //  记录本次增重时间戳
            weightedRoundRobin.setLastUpdate(now);
            // 若当前轮询权重当前值比当前记录的最大current还大，则记录下这个值及invoker
            if (cur > maxCurrent) {
                // 更新最大当前值
                maxCurrent = cur;
                selectedInvoker = invoker;
                selectedWRR = weightedRoundRobin;
            }
            // 计算主机权重total
            totalWeight += weight;
        }  // end-for

        // 若updateLock为false(即更新锁是打开状态)，
        // 且当前所有可用的invoker数量与缓存中invoker的数量不相同，则进行如下处理
        // 问题：这个“不相同”的情况有几种可能？
        // 1)invokers.size() > map.size()
        // 2)invokers.size() < map.size()
        // 分析：
        // 1> 若有新增的invoker，则经过前面的for，已经会为这些新增invoker创建相应的轮询实例，
        // 将其其放入到了缓存map中。所以对于这种有新增invoker的情况，代码走到这里，其invokers的
        // 数量也是与map的size()相同的。故，第1）种情况不可能出现
        // 2> 第2）种情况描述的是什么场景？有invoker出现了宕机的情况
        if (!updateLock.get() && invokers.size() != map.size()) {
            // 上锁
            if (updateLock.compareAndSet(false, true)) {
                try {
                    // copy -> modify -> update reference  ==> 目的是为了“迭代稳定性”
                    // 新建一个map
                    ConcurrentMap<String, WeightedRoundRobin> newMap = new ConcurrentHashMap<String, WeightedRoundRobin>();
                    // 使用老map初始化新map
                    newMap.putAll(map);
                    // 迭代新map
                    Iterator<Entry<String, WeightedRoundRobin>> it = newMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String, WeightedRoundRobin> item = it.next();
                        // item.getValue().getLastUpdate() 获取当前迭代元素的最后增重时间戳
                        // now - item.getValue().getLastUpdate() 当前迭代元素已经有多久没有更新时间戳了
                        // if()条件用于判断停更时长是否超过了 回收期
                        // 若超过了，则说明当前迭代invoker已经挂了，需要从map中删除
                        if (now - item.getValue().getLastUpdate() > RECYCLE_PERIOD) {
                            it.remove();
                        }
                    }
                    // 将更新过的新map替换掉缓存Map中的老map
                    methodWeightMap.put(key, newMap);
                } finally {
                    // 解锁
                    updateLock.set(false);
                }
            }
        }

        // 若用于记录轮询权重最大的invoker的变量selectedInvoker不为null，则返回这个invoker
        // 但是在返回之前，先让其当前值变为最小
        if (selectedInvoker != null) {
            // 轮询权重当前值减重变为最小
            selectedWRR.sel(totalWeight);
            return selectedInvoker;
        }
        // should not happen here
        return invokers.get(0);
    }

}
