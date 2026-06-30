/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.dto;

import lombok.Data;

import java.util.List;

@Data
public class RagQueryRequest {
    private String question;
    private List<Long> kbIds;
}