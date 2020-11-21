package com.yw.provider;

import com.yw.service.SomeService;

/**
 * Created by hp on 2019/4/24.
 */
public class ZhifubaoServiceImpl implements SomeService {
    @Override
    public String hello(String name) {
        System.out.println("使用【支付宝】支付");
        return "ZhifubaoServiceImpl";
    }
}
