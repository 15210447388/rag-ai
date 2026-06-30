/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.dto;

import lombok.Data;

@Data
public class IndexStatusResponse {
    private Long docId;
    private String fileName;
    private String status;         // PENDING / PROCESSING / DONE / FAILED
    private String errorMsg;
    private Integer chunkCount;
    private Integer tokenCount;
    private String indexedAt;
    private Integer retryCount;
}
