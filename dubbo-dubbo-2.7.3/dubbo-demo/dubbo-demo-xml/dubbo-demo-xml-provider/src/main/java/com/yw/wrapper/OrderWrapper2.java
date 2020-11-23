package com.yw.wrapper;

import com.yw.spi.Order;
import org.apache.dubbo.common.URL;

/**
 * @author yangwei
 * @date 2020-11-21 20:58
 */
public class OrderWrapper2 implements Order {
    private Order order;
    public OrderWrapper2(Order order) {
        this.order = order;
    }
    @Override
    public String way() {
        System.out.println("before OrderWrapper2 对 way() 的增强");
        String way = order.way();
        System.out.println("after OrderWrapper2 对 way() 的增强");
        return way;
    }
    @Override
    public String pay(URL url) {
        System.out.println("before OrderWrapper2 对 pay() 的增强");
        String pay = order.pay(url);
        System.out.println("after OrderWrapper2 对 pay() 的增强");
        return pay;
    }
}
