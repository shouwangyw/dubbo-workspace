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
import org.apache.dubbo.rpc.RpcStatus;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * LeastActiveLoadBalance
 * <p>
 * Filter the number of invokers with the least number of active calls and count the weights and quantities of these invokers.
 * If there is only one invoker, use the invoker directly;
 * if there are multiple invokers and the weights are not the same, then random according to the total weight;
 * if there are multiple invokers and the same weight, then randomly called.
 */
public class LeastActiveLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "leastactive";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int length = invokers.size();
        int leastActive = -1;   // 最小活跃度
        int leastCount = 0;     // 具有最小活跃度invoker的数量
        int[] leastIndexes = new int[length];   // 该数组中存放的是具有最小活跃度的invoker的索引
        int[] weights = new int[length];    // 该数组存放每个invoker的权重
        int totalWeight = 0;    // 具有最小活跃度的invoker的权重之和
        int firstWeight = 0;
        boolean sameWeight = true;  // 用于标识 具有最小活跃度的所有invoker的权重是否相同

        for (int i = 0; i < length; i++) {  // 遍历所有invoker，过滤出所有具有最小活跃度的invoker
            Invoker<T> invoker = invokers.get(i);
            // 获取当前遍历invoker的活跃度(默认值为0)
            int active = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName()).getActive();
            int afterWarmup = getWeight(invoker, invocation);   // 获取权重
            weights[i] = afterWarmup;
            if (leastActive == -1 || active < leastActive) {   // 处理找到更小活跃度后的重置
                // 更新最小活跃度
                leastActive = active;
                // 重置计数器
                leastCount = 1;
                leastIndexes[0] = i;
                // 重置权重和
                totalWeight = afterWarmup;
                firstWeight = afterWarmup;
                sameWeight = true;
            } else if (active == leastActive) {  // 处理找到与最小活跃度相同活跃度的invoker的累计
                leastIndexes[leastCount++] = i; // leaseCount计数器增一
                totalWeight += afterWarmup;
                if (sameWeight && i > 0 && afterWarmup != firstWeight) {
                    sameWeight = false;
                }
            }
        }  // end-for
        if (leastCount == 1) {// 若具有最小活跃度的invoker就一个，则直接返回这个即可
            return invokers.get(leastIndexes[0]);
        }
        if (!sameWeight && totalWeight > 0) {
            int offsetWeight = ThreadLocalRandom.current().nextInt(totalWeight);
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexes[i];
                offsetWeight -= weights[leastIndex];
                if (offsetWeight < 0) {
                    return invokers.get(leastIndex);
                }
            }
        }
        return invokers.get(leastIndexes[ThreadLocalRandom.current().nextInt(leastCount)]);
    }
}