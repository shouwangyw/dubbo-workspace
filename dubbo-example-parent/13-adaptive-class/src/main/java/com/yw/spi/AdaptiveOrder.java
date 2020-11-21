package com.yw.spi;

import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.StringUtils;

/**
 * @author yangwei
 * @date 2020-11-21 17:47
 */
@Adaptive
public class AdaptiveOrder implements Order {
    private String defaultName;

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    @Override
    public String way() {
        ExtensionLoader<Order> loader = ExtensionLoader.getExtensionLoader(Order.class);
        Order order = StringUtils.isEmpty(defaultName) ? loader.getDefaultExtension() : loader.getExtension(defaultName);
        return order.way();
    }
}
