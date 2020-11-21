package com.yw.provider;

import com.yw.service.SomeService;

/**
 * 业务接口实现类
 */
public class SomeServiceImpl implements SomeService{
    @Override
    public String hello(String name){
        System.out.println(name + "，我是提供者");
        return "Hello Dubbo World! " + name;
    }
}
