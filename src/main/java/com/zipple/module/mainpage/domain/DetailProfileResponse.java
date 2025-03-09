package com.zipple.module.mainpage.domain;

import com.zipple.module.mypage.agent.portfolio.domain.PortfolioProfile;
import com.zipple.module.review.domain.ReviewResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "공인중개사 상세 프로필 요청 데이터")
public class DetailProfileResponse {

    @Schema(description = "포트폴리오 제목", example = "포트폴리오 제목")
    private String title;

    @Schema(description = "외부 링크", example = "https://github.com/ARProxy")
    private String externalLink;

    @Schema(description = "중개사 이름", example = "권수연")
    private String agentName;

    @Schema(description = "상호명", example = "수연 부동산")
    private String businessName;

    @Schema(description = "거주 타입", example = "아파트")
    private String agentSpecialty;

    @Schema(description = "중개 등록번호", example = "12-3214-213124")
    private String agentRegistrationNumber;

    @Schema(description = "대표 이름", example = "권동휘")
    private String ownerName;

    @Schema(description = "별점 평균", example = "3.5")
    private Double starRating;

    @Schema(description = "대표 연락처", example = "010-2213-0123")
    private String ownerContactNumber;

    @Schema(description = "사무실 주소", example = "서울특별시 역삼3동")
    private String officeAddress;

    @Schema(description = "포트폴리오 정보들")
    private List<PortfolioProfile> portfolios;

    @Schema(description = "리뷰 정보들")
    private List<ReviewResponse> reviews;
}
