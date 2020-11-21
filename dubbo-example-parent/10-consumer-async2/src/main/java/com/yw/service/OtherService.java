package com.yw.service;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hp on 2019/4/28.
 */
public interface OtherService {
    String doFirst();
    String doSecond();

    CompletableFuture<String> doThird();
    CompletableFuture<String> doFourth();
}
