/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.dto;

import lombok.Data;

// ======================== 请求 DTO ========================

@Data
public class KnowledgeBaseCreateRequest {
    private String name;
    private String description;
    private String departmentId;
    private Boolean isPublic = false;
}


