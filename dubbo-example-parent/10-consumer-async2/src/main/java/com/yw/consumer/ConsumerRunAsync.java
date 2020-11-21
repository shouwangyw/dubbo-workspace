package com.yw.consumer;

import com.yw.service.OtherService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CompletableFuture;

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
        CompletableFuture<String> doThirdFuture = service.doThird();
        CompletableFuture<String> doFourthFuture = service.doFourth();

        long asyncInvokeTime = System.currentTimeMillis() - asyncStart;
        System.out.println("两个异步调用共计用时（毫秒）：" + asyncInvokeTime);

        doThirdFuture.whenComplete((result, throwable) -> {
            if(throwable != null){
                throwable.printStackTrace();
            }else {
                System.out.println("异步调用提供者的doThird()返回值：" + result);
            }
        });
        doFourthFuture.whenComplete((result, throwable) -> {
            if(throwable != null){
                throwable.printStackTrace();
            }else {
                System.out.println("异步调用提供者的doFourth()返回值：" + result);
            }
        });

        System.out.println("获取到两个异步操作共计用时（毫秒）：" + (System.currentTimeMillis() - asyncStart));
    }
}
