/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.controller;

import com.ragai.dto.ApiResponse;
import com.ragai.dto.RagQueryRequest;
import com.ragai.dto.RagResponse;
import com.ragai.service.FullRagPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagQueryController {

    private final FullRagPipeline fullRagPipeline;

    @PostMapping("/query")
    public ApiResponse<RagResponse> query(@RequestBody RagQueryRequest req) {
        return ApiResponse.ok(fullRagPipeline.query(req.getQuestion(), req.getKbIds()));
    }
}