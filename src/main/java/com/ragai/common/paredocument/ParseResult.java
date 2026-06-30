/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-12
 * Description:
 */
package com.ragai.common.paredocument;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 文件解析统一结果接受类
 */
@Data
@Builder
public class ParseResult {

    /** 解析是否成功 */
    private boolean success;

    /** 错误信息（success=false 时有值） */
    private String errorMsg;

    /** 解析出的页面列表（PDF 按页，其他格式整体算一页） */
    private List<PageContent> pages;

    /** 文档总页数 */
    private int totalPages;

    /** 文档标题（如果能识别） */
    private String title;

    @Data
    @Builder
    public static class PageContent {
        /** 页码（1-based） */
        private int pageNum;
        /** 该页的纯文本内容 */
        private String text;
        /** 该页识别到的章节标题（可能为空） */
        private String sectionTitle;
    }

    public static ParseResult failure(String errorMsg) {
        return ParseResult.builder()
                .success(false)
                .errorMsg(errorMsg)
                .pages(List.of())
                .build();
    }

    /** 获取所有页的合并文本 */
    public String getFullText() {
        if (pages == null) return "";
        return pages.stream()
                .map(PageContent::getText)
                .filter(t -> t != null && !t.isBlank())
                .reduce("", (a, b) -> a + "\n\n" + b)
                .strip();
    }
}