package com.zipple.module.review.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    @Schema(description = "리뷰 ID", example = "1")
    private Long reviewId;

    @Schema(description = "프로필 사진", example = "https://...")
    private String profileUrl;

    @Schema(description = "닉네임", example = "짱구")
    private String nickname;

    @Schema(description = "별점", example = "4")
    private Integer starCount;

    @Schema(description = "내용", example = "너무 좋은 사람 권수연")
    private String content;

    @Schema(description = "작성일")
    private String createdAt;

    @Schema(description = "수정일")
    private String updatedAt;
}
