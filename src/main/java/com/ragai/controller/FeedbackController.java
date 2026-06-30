package com.ragai.controller;

import com.ragai.dto.ApiResponse;
import com.ragai.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/{messageId}")
    public ApiResponse<Void> submitFeedback(
            @PathVariable Long messageId,
            @RequestParam int feedback,
            @RequestParam(required = false) String comment) {
        feedbackService.submitFeedback(messageId, feedback, comment);
        return ApiResponse.ok(null);
    }
}