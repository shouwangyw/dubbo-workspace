package com.yw.provider;

import com.yw.service.SomeService;

/**
 * Created by hp on 2019/4/24.
 */
public class OldServiceImpl implements SomeService {
    @Override
    public String hello(String name) {
        System.out.println("执行【老】的提供者OldServiceImpl的hello()");
        return "OldServiceImpl";
    }
}
