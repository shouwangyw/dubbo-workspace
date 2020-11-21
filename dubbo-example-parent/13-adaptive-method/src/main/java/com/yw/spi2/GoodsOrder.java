package com.yw.spi2;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * @author yangwei
 * @date 2020-11-21 17:35
 */
@SPI("wechat")
public interface GoodsOrder {
    String way();

    @Adaptive
    String pay(URL url);
}