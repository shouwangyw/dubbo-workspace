package com.yw.consumer;

import com.yw.service.OtherService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 运行测试类
 *
 * @author yangwei
 */
public class ConsumerRunAsync {
    public static void main(String[] args){
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-consumer.xml");
        OtherService service = (OtherService) ac.getBean("otherService");

        // 记录异步调用开始时间
        long asyncStart = System.currentTimeMillis();

        // 异步调用
        service.doThird();
        service.doFourth();

        System.out.println("两个异步操作共计用时（毫秒）：" + (System.currentTimeMillis() - asyncStart));
    }
}
