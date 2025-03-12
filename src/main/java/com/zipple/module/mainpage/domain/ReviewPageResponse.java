package com.zipple.module.mainpage.domain;

import com.zipple.module.mypage.agent.portfolio.domain.PortfolioMainImage;
import com.zipple.module.review.domain.ReviewResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewPageResponse {
    @Schema(description = "포트폴리오 데이터 리스트")
    private List<ReviewResponse> content;

    @Schema(description = "총 개수", example = "20")
    private Long totalElements;

    @Schema(description = "총 페이지 수", example = "3")
    private Integer totalPages;

    @Schema(description = "현재 페이지 번호", example = "0")
    private Integer currentPage;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private Boolean isLast;
}
