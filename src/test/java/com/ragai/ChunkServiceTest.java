/*
 * Copyright (c) 2026 ragai RAG知识库
 * All rights reserved.
 * Author: 陈斌
 * Create Date: 2026-02-05
 * Description:
 */
package com.ragai;

import com.ragai.common.docsplitter.ChunkResult;
import com.ragai.common.paredocument.ParseResult;
import com.ragai.service.ChunkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ChunkServiceTest {

    @Autowired
    private ChunkService chunkService;

    @Test
    void chunksNotTooLargeOrTooSmall() {
        String longText = "这是一段测试文本。".repeat(200); // 约 1800 字符
        ParseResult result = ParseResult.builder()
                .success(true)
                .pages(List.of(ParseResult.PageContent.builder()
                        .pageNum(1)
                        .text(longText)
                        .build()))
                .totalPages(1)
                .build();

        List<ChunkResult> chunks = chunkService.chunk(result);

        assertThat(chunks).isNotEmpty();
        for (ChunkResult chunk : chunks) {
            // 每块不应超过 chunkSize（findGoodBreakPoint 只向前回退断点，不会超出上限）
            assertThat(chunk.getContent().length()).isLessThanOrEqualTo(512);
            // 每块至少 20 字符（ChunkService 已过滤更短的碎片）
            assertThat(chunk.getContent().length()).isGreaterThanOrEqualTo(20);
        }

        // 验证相邻块有重叠
        if (chunks.size() >= 2) {
            String end0 = chunks.get(0).getContent();
            String start1 = chunks.get(1).getContent();
            // 第一块末尾的内容应该出现在第二块开头附近（重叠）
            String overlapPart = end0.substring(Math.max(0, end0.length() - 64));
            assertThat(start1).contains(overlapPart.substring(0, Math.min(30, overlapPart.length())));
        }
    }
}