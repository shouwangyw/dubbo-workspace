package com.yw.run;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 运行测试类
 */
public class ProviderRun {
    public static void main(String[] args) throws Exception{
        // 创建Spring容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-provider.xml");
        // 启动Spring容器
        ((ClassPathXmlApplicationContext)ac).start();
        // 使主线程阻塞
        System.in.read();
    }
}
