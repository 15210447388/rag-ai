/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ======================== 响应 DTO ========================
 */
@Data
@Builder
public class KnowledgeBaseVO {
    private Long id;
    private String name;
    private String description;
    private String departmentId;
    private Boolean isPublic;
    private Long createdBy;
    private LocalDateTime createdAt;
    /** 当前用户对该知识库的权限：ADMIN / WRITE / READ */
    private String permission;
}
