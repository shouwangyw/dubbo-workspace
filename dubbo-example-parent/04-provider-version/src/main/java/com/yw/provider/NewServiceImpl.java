package com.yw.provider;

import com.yw.service.SomeService;

/**
 * Created by hp on 2019/4/24.
 */
public class NewServiceImpl implements SomeService {
    @Override
    public String hello(String name) {
        System.out.println("执行【新】的提供者OldServiceImpl的hello()");
        return "NewServiceImpl";
    }
}
