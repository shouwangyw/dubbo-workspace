package com.yw;

import com.yw.spi.AdaptiveOrder;
import com.yw.spi.Order;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.junit.Test;

import java.util.Set;

/**
 * @author yangwei
 * @date 2020-11-21 10:57
 */
public class OrderTest {
    @Test
    public void test01() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        Order order = loader.getAdaptiveExtension();
        System.out.println(order.way());
    }

    @Test
    public void test02() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        Order order = loader.getAdaptiveExtension();
        ((AdaptiveOrder) order).setDefaultName("alipay");
        System.out.println(order.way());
    }
    /**
     * @Adaptive类不是直接扩展类
     */
    @Test
    public void test03() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        // 获取该SPI接口的所有直接扩展类：即该扩展类是直接对该SPI接口进行业务功能上的扩展，可以单独使用
        Set<String> extensions = loader.getSupportedExtensions();
        System.out.println(extensions);
    }
}
