package com.zipple.module.mypage.agent.portfolio.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "중개사 상세 프로필 포트폴리오 데이터")
public class PortfolioProfile {

    @Schema(description = "포트폴리오 이미지", example = "http://44.203.190.167:8081/zipple/home/ubuntu/zipple/logo/zipplelogo.png")
    private String profileImage;

    @Schema(description = "포트폴리오 제목", example = "역삼의 어느 한 빌딩")
    private String title;

    @Schema(description = "포트폴리오 생성일", example = "2025-02-09 04:47:09")
    private String createdAt;
}
