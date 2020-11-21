package com.yw.provider;

import com.yw.service.SomeService;

/**
 * Created by hp on 2019/4/24.
 */
public class WeixinServiceImpl implements SomeService {
    @Override
    public String hello(String name) {
        System.out.println("使用【微信】支付");
        return "WeixinServiceImpl";
    }
}
