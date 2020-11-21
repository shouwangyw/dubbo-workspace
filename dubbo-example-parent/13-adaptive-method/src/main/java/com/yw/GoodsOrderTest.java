package com.yw;

import com.yw.spi2.GoodsOrder;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.junit.Test;

/**
 * @author yangwei
 * @date 2020-11-21 10:57
 */
public class GoodsOrderTest {
    @Test
    public void test01() {
        // 获取到SPI接口Order的loader实例
        ExtensionLoader<GoodsOrder> loader = ExtensionLoader.getExtensionLoader(GoodsOrder.class);
        // 获取到Order的自适应实例
        GoodsOrder order = loader.getAdaptiveExtension();
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx");
        System.out.println(order.pay(url));
        System.out.println(order.way());    // 会报错
    }

    @Test
    public void test02() {
        // 获取到SPI接口Order的loader实例
        ExtensionLoader<GoodsOrder> loader = ExtensionLoader.getExtensionLoader(GoodsOrder.class);
        // 获取到Order的自适应实例
        GoodsOrder order = loader.getAdaptiveExtension();
        URL url = URL.valueOf("xxx://localhost:8080/ooo/xxx?goods.order=alipay");
        System.out.println(order.pay(url));
//        System.out.println(order.way()); // 报错
    }
}
