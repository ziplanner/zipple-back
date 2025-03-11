package com.zipple.module.mainpage.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "중개사 매칭 데이터")
public class AgentMatchingResponse {

    @Schema(description = "중개사 UUID", example = "b0ki9FVoNhqM30rSKZ9tIw")
    private String agentId;

    @Schema(description = "프로필 이미지", example = "http://44.203.190.167:8081/zipple/97ab79c6-e12b-49bf-bb86-abaff6fb6155_Frame 4195.png")
    private String profileUrl;

    @Schema(description = "거주 타입", example = "아파트")
    private String agentSpecialty;

    @Schema(description = "포트폴리오 개수", example = "20")
    private Integer portfolioCount;

    @Schema(description = "중개사 이름", example = "권동휘")
    private String agentName;

    @Schema(description = "자기소개 제목", example = "안녕하세요")
    private String title;

    @Schema(description = "별점 평균", example = "3.5")
    private Double starRating;

    @Schema(description = "좋아요 갯수", example = "4")
    private Integer likeCount;

    @Schema(description = "좋아요 여부", example = "true")
    private Boolean liked;

    @Schema(description = "리뷰 갯수", example = "21")
    private Integer reviewCount;

    @Schema(description = "1인 가구 전문가", example = "true")
    private Boolean singleHouseholdExpert;
}
