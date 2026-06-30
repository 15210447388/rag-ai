/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync        // 开启异步，支持索引任务异步执行
@EnableRetry        // 开启重试，支持 Embedding 调用失败重试
@EnableCaching
public class RagAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagAiApplication.class, args);
    }

}