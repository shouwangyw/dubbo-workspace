package com.yw.provider;

import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * 定义一个泛化服务：通用服务、一般性服务
 *
 * @Author yangwei
 * @create 2020/11/26 10:11
 */
public class GenericServiceImpl implements GenericService {
    /**
     * @param method         消费者通过远程调用的方法名
     * @param parameterTypes 方法参数类型列表
     * @param args           消费者通过远程调用传递来的实参值
     */
    @Override
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        // 根据不同的调用方法，实现不同的服务
        if ("hello".equals(method)) {
            return "Generic hello, " + args[0];
        }
        return null;
    }
}
