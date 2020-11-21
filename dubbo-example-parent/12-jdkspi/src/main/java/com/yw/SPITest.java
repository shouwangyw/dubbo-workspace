package com.yw;

import com.yw.service.SomeService;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author yangwei
 */
public class SPITest {
    public static void main(String[] args) {
        // 加载提供者配置文件，创建提供者类加载器
        ServiceLoader<SomeService> loader = ServiceLoader.load(SomeService.class);
        // ServiceLoader本身就是一个迭代器
        Iterator<SomeService> it = loader.iterator();
        // 迭代加载每一个实现类，并生成每一个提供者对象
        while (it.hasNext()) {
            SomeService service = it.next();
            service.doSome();
        }
    }
}
