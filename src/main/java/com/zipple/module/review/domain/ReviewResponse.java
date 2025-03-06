package com.zipple.module.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long reviewId;
    private String profileUrl;
    private String nickname;
    private String content;
    private String createdAt;
    private String updatedAt;

}
