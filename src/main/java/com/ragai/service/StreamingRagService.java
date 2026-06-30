package com.ragai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ragai.common.RagPromptTemplate;
import com.ragai.config.ConfidenceFilter;
import com.ragai.config.TokenMetrics;
import com.ragai.dto.RagResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingRagService {

    private final EnhancedRetrieverService enhancedRetriever;
    private final RerankerService rerankerService;
    private final ConfidenceFilter confidenceFilter;
    private final ContextTrimmerService contextTrimmer;
    private final SourceBuilder sourceBuilder;
    private final ChatSessionService sessionService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final TokenMetrics tokenMetrics;
    private final QueryCacheService queryCacheService;

    public void streamQuery(String question, List<Long> kbIds, String sessionId, SseEmitter emitter) {
        long start = System.currentTimeMillis();

        try {
            // 先查缓存——命中则直接把完整答案一次性推给前端，跳过检索和生成
            RagResponse cached = queryCacheService.getFromCache(question, kbIds);
            if (cached != null) {
                emitter.send(SseEmitter.event().name("token").data(cached.getAnswer()));
                String doneData = objectMapper.writeValueAsString(
                        new DonePayload(cached.getSources(), 0));
                emitter.send(SseEmitter.event().name("done").data(doneData));
                emitter.complete();
                sessionService.saveMessage(sessionId, question, cached.getAnswer(),
                        sourceBuilder.sourcesToJson(cached.getSources()), 0);
                return;
            }

            // 缓存未命中，走完整流式管道
            emitter.send(SseEmitter.event()
                    .name("status")
                    .data("{\"type\":\"RETRIEVING\",\"message\":\"正在检索知识库...\"}"));

            var candidates = enhancedRetriever.retrieveWithHyde(question, kbIds, 20);
            var reranked = rerankerService.rerank(question, candidates, 5);
            var filtered = confidenceFilter.filter(reranked);

            if (filtered.isEmpty()) {
                sendNotFound(emitter);
                return;
            }

            var trimmed = contextTrimmer.trim(filtered);

            emitter.send(SseEmitter.event()
                    .name("status")
                    .data("{\"type\":\"GENERATING\",\"message\":\"已找到相关内容，正在生成回答...\"}"));

            String context = buildContext(trimmed);
            String systemPrompt = RagPromptTemplate.buildSystemPrompt(context, trimmed.size());

            StringBuilder fullAnswer = new StringBuilder();

            chatClient.prompt()
                    .system(systemPrompt)
                    .user(question)
                    .stream()
                    .content()
                    .doOnNext(token -> {
                        try {
                            fullAnswer.append(token);
                            emitter.send(SseEmitter.event().name("token").data(token));
                        } catch (IOException e) {
                            log.warn("[StreamRAG] SSE 推送 Token 失败，客户端可能已断开");
                            throw new RuntimeException("SSE 连接断开");
                        }
                    })
                    .blockLast();

            String answer = fullAnswer.toString();
            tokenMetrics.recordGenerationTokens(contextTrimmer.countTokens(answer));

            List<RagResponse.Source> sources = sourceBuilder.buildSources(answer, trimmed);
            String sourcesJson = sourceBuilder.sourcesToJson(sources);
            int latencyMs = (int) (System.currentTimeMillis() - start);

            sessionService.saveMessage(sessionId, question, answer, sourcesJson, latencyMs);

            // 写入缓存
            RagResponse response = RagResponse.builder()
                    .answer(answer).sources(sources).latencyMs(latencyMs).build();
            queryCacheService.putToCache(question, kbIds, response);

            String doneData = objectMapper.writeValueAsString(new DonePayload(sources, latencyMs));
            emitter.send(SseEmitter.event().name("done").data(doneData));
            emitter.complete();

        } catch (Exception e) {
            log.error("[StreamRAG] 流式查询异常：{}", e.getMessage(), e);
            try {
                emitter.send(SseEmitter.event().name("error")
                        .data("{\"message\":\"生成过程中出现异常\"}"));
                emitter.complete();
            } catch (IOException ignored) {}
        }
    }

    public RagResponse syncQuery(String question, List<Long> kbIds, String sessionId) {
        // 先查缓存
        RagResponse cached = queryCacheService.getFromCache(question, kbIds);
        if (cached != null) {
            sessionService.saveMessage(sessionId, question, cached.getAnswer(),
                    sourceBuilder.sourcesToJson(cached.getSources()), 0);
            return cached;
        }

        // 缓存未命中，走完整 RAG 管道
        long start = System.currentTimeMillis();

        var candidates = enhancedRetriever.retrieveWithHyde(question, kbIds, 20);
        var reranked = rerankerService.rerank(question, candidates, 5);
        var filtered = confidenceFilter.filter(reranked);

        if (filtered.isEmpty()) return RagResponse.notFound();

        var trimmed = contextTrimmer.trim(filtered);
        String context = buildContext(trimmed);
        String systemPrompt = RagPromptTemplate.buildSystemPrompt(context, trimmed.size());

        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();

        tokenMetrics.recordGenerationTokens(contextTrimmer.countTokens(answer));

        List<RagResponse.Source> sources = sourceBuilder.buildSources(answer, trimmed);
        String sourcesJson = sourceBuilder.sourcesToJson(sources);
        int latencyMs = (int) (System.currentTimeMillis() - start);

        sessionService.saveMessage(sessionId, question, answer, sourcesJson, latencyMs);

        RagResponse response = RagResponse.builder()
                .answer(answer)
                .sources(sources)
                .latencyMs(latencyMs)
                .build();

        // 写入缓存
        queryCacheService.putToCache(question, kbIds, response);
        return response;
    }

    private String buildContext(List<HybridRetrieverService.ScoredChunk> chunks) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            var sc = chunks.get(i);
            sb.append("[参考").append(i + 1).append("]");
            if (sc.chunk().getSectionTitle() != null) sb.append(" ").append(sc.chunk().getSectionTitle());
            sb.append("\n").append(sc.content()).append("\n\n");
        }
        return sb.toString().strip();
    }

    private void sendNotFound(SseEmitter emitter) throws IOException {
        String msg = "在知识库中未找到与该问题相关的内容。请尝试用不同关键词提问，或联系相关部门。";
        emitter.send(SseEmitter.event().name("token").data(msg));
        emitter.send(SseEmitter.event().name("done").data("{\"sources\":[]}"));
        emitter.complete();
    }



    record DonePayload(List<RagResponse.Source> sources, int latencyMs) {}
}