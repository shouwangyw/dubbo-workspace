package com.yw.service;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hp on 2019/4/28.
 */
public interface OtherService {
    String doFirst();
    String doSecond();

    /**
     * 异步调用返回结果
     */
    CompletableFuture<String> doThird();
    CompletableFuture<String> doFourth();
}
