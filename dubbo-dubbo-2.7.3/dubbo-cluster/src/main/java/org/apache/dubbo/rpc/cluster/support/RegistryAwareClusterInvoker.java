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
package org.apache.dubbo.rpc.cluster.support;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_KEY;
import static org.apache.dubbo.common.constants.RegistryConstants.REGISTRY_KEY;

/**
 *
 */
public class RegistryAwareClusterInvoker<T> extends AbstractClusterInvoker<T> {

    private static final Logger logger = LoggerFactory.getLogger(RegistryAwareClusterInvoker.class);

    public RegistryAwareClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Result doInvoke(Invocation invocation, final List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        // First, pick the invoker (XXXClusterInvoker) that comes from the local registry, distinguish by a 'default' key.
        // 遍历所有invoker
        for (Invoker<T> invoker : invokers) {
            // 若当前invoker可用，且来自于默认注册中心，则直接返回
            if (invoker.isAvailable() && invoker.getUrl().getParameter(REGISTRY_KEY + "." + DEFAULT_KEY, false)) {
                return invoker.invoke(invocation);
            }
        }
        // If none of the invokers has a local signal, pick the first one available.
        // 若默认注册中心没有可用的invoker，则从所有invoker中查找第一个可用的直接返回
        for (Invoker<T> invoker : invokers) {
            if (invoker.isAvailable()) {
                return invoker.invoke(invocation);
            }
        }

        // 对于所有invoker都不可用的情况，抛出异常，进行降级
        throw new RpcException("No provider available in " + invokers);
    }
}
