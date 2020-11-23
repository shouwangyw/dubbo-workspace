package com.yw.spi;

import org.apache.dubbo.common.extension.ExtensionLoader;

public class Order$Adaptive implements com.yw.spi.Order {
    public java.lang.String way() {
        throw new UnsupportedOperationException("The method public abstract java.lang.String com.yw.spi.Order.way() of interface com.yw.spi.Order is not adaptive method!");
    }

    public java.lang.String pay(org.apache.dubbo.common.URL arg0) {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        org.apache.dubbo.common.URL url = arg0;
        String extName = url.getParameter("order", "wechat");
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (com.yw.spi.Order) name from url (" + url.toString() + ") use keys([order])");
        com.yw.spi.Order extension = (com.yw.spi.Order) ExtensionLoader.getExtensionLoader(com.yw.spi.Order.class).getExtension(extName);
        return extension.pay(arg0);
    }
}