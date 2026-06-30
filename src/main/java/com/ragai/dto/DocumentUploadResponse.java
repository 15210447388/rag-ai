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
public class DocumentUploadResponse {
    private Long docId;
    private String fileName;
    private String status;       // PENDING（已提交，等待索引）
    private String message;

    public static DocumentUploadResponse submitted(Long docId, String fileName) {
        DocumentUploadResponse r = new DocumentUploadResponse();
        r.setDocId(docId);
        r.setFileName(fileName);
        r.setStatus("PENDING");
        r.setMessage("文档已上传，正在后台索引，请通过 /status 接口查询进度");
        return r;
    }
}
