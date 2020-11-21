package com.yw.spi;

import org.apache.dubbo.common.extension.Activate;

/**
 * @author yangwei
 * @date 2020-11-21 21:17
 */
@Activate(group = "offline", order = 5)
public class CouponOrder implements Order {
    @Override
    public String way() {
        System.out.println("--- 使用购物券支付 ---");
        return "购物券支付";
    }
}
