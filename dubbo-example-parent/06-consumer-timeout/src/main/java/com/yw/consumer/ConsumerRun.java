package com.yw.consumer;

import com.yw.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 运行测试类
 *
 * @author yangwei
 */
public class ConsumerRun {
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-consumer.xml");
        UserService service = (UserService) ac.getBean("userService");

        // 有返回值的方法降级结果为null
        String username = service.getUsernameById(3);
        System.out.println(username);
    }
}
