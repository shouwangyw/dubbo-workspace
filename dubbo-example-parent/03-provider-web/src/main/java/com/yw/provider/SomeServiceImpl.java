package com.yw.provider;

import com.yw.service.SomeService;

/**
 * 业务接口实现类
 */
public class SomeServiceImpl implements SomeService{
    @Override
    public String hello(String name){
        System.out.println("执行提供者的hello() " + name);
        return "hello, " + name;
    }
}
