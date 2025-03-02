package com.zipple.module.review;

import com.zipple.module.mainpage.domain.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
@Tag(name = "리뷰")
public class ReviewController {

    private final ReviewService reviewService;


    @Operation(summary = "공인 중개사 리뷰")
    @GetMapping(value = "/{agentId}/review")
    public ResponseEntity<List<ReviewResponse>> getReview(
            @Parameter(name = "agentId", description = "리뷰 달린 중개사 아이디")
            @PathVariable(value = "agentId") Long userId
    ) {
        return ResponseEntity.ok(reviewService.getReview(userId));
    }
}
