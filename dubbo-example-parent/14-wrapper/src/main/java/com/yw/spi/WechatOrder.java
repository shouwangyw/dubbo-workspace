package com.yw.spi;

import org.apache.dubbo.common.URL;

/**
 * @author yangwei
 * @date 2020-11-21 10:52
 */
public class WechatOrder implements Order {
    @Override
    public String way() {
        System.out.println("--- 使用微信支付 ---");
        return "微信支付";
    }

    @Override
    public String pay(URL url) {
        System.out.println("---  pay 使用微信支付  ---");
        return "pay 微信支付";
    }
}
