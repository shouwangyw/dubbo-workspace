package com.yw.spi;

import org.apache.dubbo.common.extension.SPI;

/**
 * 下单接口
 *
 * @author yangwei
 */
@SPI("alipay")
public interface Order {
    /**
     * 支付方式
     */
    String way();
}
