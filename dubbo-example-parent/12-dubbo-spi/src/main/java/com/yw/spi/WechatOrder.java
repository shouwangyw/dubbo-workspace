package com.yw.spi;

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
}
