package com.zipple.module.review;

import com.zipple.module.mainpage.domain.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/review")
@Tag(name = "리뷰")
public class ReviewController {

    private final ReviewService reviewService;


    @Operation(summary = "공인 중개사 리뷰")
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReview(@RequestParam(value = "userId")  Long userId) {
        return ResponseEntity.ok(reviewService.getReview(userId));
    }
}
