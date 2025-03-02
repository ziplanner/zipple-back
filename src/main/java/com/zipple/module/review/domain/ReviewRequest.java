package com.zipple.module.review.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewRequest {

    @Schema(description = "리뷰 내용", example = "너무 좋은 사람")
    private String content;

    @Schema(description = "별점", example = "4")
    private Integer starCount;
}
