package com.yw.consumer;

import com.yw.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 运行测试类
 */
public class ConsumerRun {
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-consumer.xml");
        UserService service = (UserService) ac.getBean("userService");

        // 有返回值的方法降级结果为null
        String usernameById = service.getUsernameById(3);
        System.out.println(usernameById);

        // 无返回值的方法降级结果是无任何显示
        service.addUser("China");
    }
}
