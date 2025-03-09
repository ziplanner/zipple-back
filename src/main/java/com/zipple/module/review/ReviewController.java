package com.zipple.module.review;

import com.zipple.module.review.domain.ReviewRequest;
import com.zipple.module.review.domain.ReviewResponse;
import com.zipple.module.review.entity.Review;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/reviews")
@Tag(name = "리뷰")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "공인중개사에 대한 리뷰를 작성합니다.")
    @PostMapping("/{agentId}")
    public ResponseEntity<Review> createReview(
            @Parameter(name = "agentId", description = "리뷰 달릴 중개사")
            @PathVariable(value = "agentId") String agentId,
            @RequestBody ReviewRequest reviewRequest
    ) {
        return ResponseEntity.ok(reviewService.createReview(agentId, reviewRequest));
    }

    @Hidden
    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @Parameter(name = "reviewId", description = "리뷰 달릴 중개사")
            @PathVariable(value = "reviewId") Long reviewId,
            @RequestBody ReviewRequest reviewRequest
            ) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewRequest));
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @Parameter(name = "reviewId", description = "리뷰 달릴 중개사")
            @PathVariable(value = "reviewId") Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    @Operation(summary = "공인중개사 리뷰 조회", description = "특정 공인중개사의 모든 리뷰를 조회합니다.")
    @GetMapping("/{agentId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByAgent(@PathVariable(value = "agentId") String agentId) {
        return ResponseEntity.ok(reviewService.getReviewsByAgent(agentId));
    }

    @Hidden
    @Operation(summary = "공인중개사 평균 별점 조회", description = "특정 공인중개사의 평균 별점을 조회합니다.")
    @GetMapping("/{agentId}/average")
    public ResponseEntity<Double> getAverageStarCount(@PathVariable(value = "agentId") Long agentId) {
        return ResponseEntity.ok(reviewService.getAverageStarCount(agentId));
    }
}
