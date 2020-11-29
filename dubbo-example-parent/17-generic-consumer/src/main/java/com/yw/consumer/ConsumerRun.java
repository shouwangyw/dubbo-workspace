package com.yw.consumer;

import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author yangwei
 * @create 2020/11/26 10:24
 */
public class ConsumerRun {
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-consumer.xml");
        GenericService service = (GenericService) ac.getBean("someService");
        Object hello = service.$invoke("hello", new String[]{String.class.getName()}, new Object[]{"Tom"});
        System.out.println("generic =============== " + hello);
    }
}
