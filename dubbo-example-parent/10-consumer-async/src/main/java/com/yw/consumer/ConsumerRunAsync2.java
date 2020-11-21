package com.yw.consumer;

import com.yw.service.OtherService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Future;

/**
 * 运行测试类
 *
 * @author yangwei
 */
public class ConsumerRunAsync2 {
    public static void main(String[] args) throws Exception{
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-consumer.xml");
        OtherService service = (OtherService) ac.getBean("otherService");

        // 记录异步调用开始时间
        long asyncStart = System.currentTimeMillis();

        // 异步调用
        String result1 = service.doThird();
        System.out.println("调用结果1 = " + result1);

        Future<String> thirdFuture = RpcContext.getContext().getFuture();
        // 阻塞
        String result2 = thirdFuture.get();
        System.out.println("调用结果2 = " + result2);

        System.out.println("获取到异步调用结果共计用时（毫秒）：" + (System.currentTimeMillis() - asyncStart));
    }
}
