/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.service;

import com.ragai.security.UserContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class IndexTaskLauncher {

    private final IndexService indexService;

    public IndexTaskLauncher(@Lazy IndexService indexService) {
        this.indexService = indexService;
    }

    @Async("indexTaskExecutor")
    public void launchFromMinio(Long taskId, Long docId,
                                Long userId, String departmentId, String role) {
        UserContext.set(userId, departmentId, role);
        try {
            indexService.executeFromMinio(taskId, docId);
        } finally {
            UserContext.clear();
        }
    }

    @Async("indexTaskExecutor")
    public void launchWithText(Long taskId, Long docId, String textContent,
                               Long userId, String departmentId, String role) {
        UserContext.set(userId, departmentId, role);
        try {
            indexService.executeWithText(taskId, docId, textContent);
        } finally {
            UserContext.clear();
        }
    }
}