package com.yw.service;

import java.util.concurrent.TimeUnit;

/**
 * @author yangwei
 */
public class OtherServiceImpl implements OtherService {
    // 耗时操作
    private void sleep(String method){
        long startTime = System.currentTimeMillis();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long useTime = endTime - startTime;
        System.out.println(method + "方法执行用时：" + useTime);
    }
    @Override
    public String doFirst() {
        sleep("doFirst()");
        return "doFirst()";
    }
    @Override
    public String doSecond() {
        sleep("doSecond()");
        return "doSecond()";
    }
    @Override
    public String doThird() {
        sleep("doThird()");
        return "doThird()";
    }
    @Override
    public String doFourth() {
        sleep("doFourth()");
        return "doFourth()";
    }
}
