package com.yw;

import com.yw.spi.Order;
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
        // 指定要加载并创建的扩展类实例
        Order alipay = loader.getExtension("alipay");
        System.out.println(alipay.way());
        Order wechat = loader.getExtension("wechat");
        System.out.println(wechat.way());

//        Order xxx = loader.getExtension("xxx");
//        System.out.println(xxx.way());
    }
    @Test
    public void test02() {
        // 获取到SPI接口Order的loader实例
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        // 以下代码会报错
        Order alipay = loader.getExtension(null);
        System.out.println(alipay.way());
    }

    @Test
    public void test03() {
        // 获取到SPI接口Order的loader实例
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        // 以下代码会报错
        Order alipay = loader.getExtension("true");
        System.out.println(alipay.way());
    }
}
