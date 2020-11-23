package com.yw;

import com.yw.spi.Order;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author yangwei
 * @date 2020-11-21 10:57
 */
public class OrderTest {
    public static void main(String[] args) {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        Order order = loader.getAdaptiveExtension();
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx");
        System.out.println(order.pay(url));
    }
}
