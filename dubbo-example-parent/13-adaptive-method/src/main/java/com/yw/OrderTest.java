package com.yw;

import com.yw.spi.Order;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.junit.Test;

/**
 * @author yangwei
 * @date 2020-11-21 10:57
 */
public class OrderTest {
    @Test
    public void test01() {
        // 获取到SPI接口Order的loader实例
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        // 获取到Order的自适应实例
        Order order = loader.getAdaptiveExtension();
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx");
        System.out.println(order.pay(url));
        System.out.println(order.way());
    }
    @Test
    public void test02() {
        // 获取到SPI接口Order的loader实例
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        // 获取到Order的自适应实例
        Order order = loader.getAdaptiveExtension();
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx?order=alipay");
        System.out.println(order.pay(url));
        System.out.println(order.way()); // 报错
    }
}
