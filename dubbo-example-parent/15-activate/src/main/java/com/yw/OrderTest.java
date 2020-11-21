package com.yw;

import com.yw.spi.Order;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @author yangwei
 * @date 2020-11-21 10:57
 */
public class OrderTest {
    @Test
    public void test01() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx");
        // 激活所有group为online的扩展类
        List<Order> online = loader.getActivateExtension(url, "", "online");
        for (Order order : online) {
            System.out.println(order.way());
        }
    }
    @Test
    public void test02() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx");
        // 激活所有group为offline的扩展类
        List<Order> offline = loader.getActivateExtension(url, "", "offline");
        for (Order order : offline) {
            System.out.println(order.way());
        }
    }
    @Test
    public void test03() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx?order=alipay");
        // 激活所有group为online的扩展类
        List<Order> online = loader.getActivateExtension(url, "order", "online");
        for (Order order : online) {
            System.out.println(order.way());
        }
    }
    @Test
    public void test04() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx?order=alipay");
        // 激活所有group为offline的扩展类
        // getActivateExtension()的后两个参数是选择激活类的两个条件，是 或 的关系
        List<Order> offline = loader.getActivateExtension(url, "order", "offline");
        for (Order order : offline) {
            System.out.println(order.way());
        }
    }
    /**
     * @Activate类是直接扩展类
     */
    @Test
    public void test05() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        // 获取该SPI接口的所有直接扩展类：即该扩展类是直接对该SPI接口进行业务功能上的扩展，可以单独使用
        Set<String> extensions = loader.getSupportedExtensions();
        System.out.println(extensions);
    }
}
