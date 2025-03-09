package com.zipple.module.mainpage.domain;

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
@Schema(description = "메인 포트폴리오 상세 조회 응답 데이터")
public class DetailPortfolioResponse {

    private String title;
    private String externalLink;
    private String content;
    private List<String> portfolioList;

}
