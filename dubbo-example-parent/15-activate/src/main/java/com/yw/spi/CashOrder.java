package com.yw.spi;

import org.apache.dubbo.common.extension.Activate;

/**
 * @author yangwei
 * @date 2020-11-21 21:16
 */
@Activate(group = "offline", order = 4)
public class CashOrder implements Order {
    @Override
    public String way() {
        System.out.println("--- 使用现金支付 ---");
        return "现金支付";
    }
}
