package com.yw.spi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * 下单接口
 *
 * @author yangwei
 */
@SPI("wechat")
public interface Order {
    String way();

    @Adaptive
    String pay(URL url);
}
