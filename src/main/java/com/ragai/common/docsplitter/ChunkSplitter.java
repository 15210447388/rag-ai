/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.common.docsplitter;

import com.ragai.common.paredocument.ParseResult;

import java.util.List;

public interface ChunkSplitter {

    /**
     * 将解析结果拆分为若干块。
     *
     * @param parseResult 文档解析结果（含多页内容）
     * @param config      分块参数
     * @return 分块列表
     */
    List<ChunkResult> split(ParseResult parseResult, ChunkConfig config);
}