package com.zipple.module.mainpage;

import com.zipple.module.mainpage.domain.DetailPortfolioResponse;
import com.zipple.module.mainpage.domain.DetailProfileResponse;
import com.zipple.module.mainpage.domain.MatchingResponse;
import com.zipple.module.member.common.entity.category.AgentType;
import com.zipple.module.mypage.agent.portfolio.domain.PortfolioPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "메인 화면")
@RestController
@RequestMapping(value = "/api/v1/main")
@RequiredArgsConstructor
public class MainPageController {

    private final MainPageService mainPageService;

    @Operation(summary = "매칭 프로필 기본 화면", description = "페이징을 이용하여 중개사의 매칭 프로필을 조회합니다.")
    @GetMapping(value = "/matching")
    public ResponseEntity<MatchingResponse> getMatchingProfile(
            @Parameter(name = "page", description = "페이지 번호", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(name = "size", description = "페이지 크기", example = "9")
            @RequestParam(value = "size", defaultValue = "9") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        MatchingResponse matchingResponse = mainPageService.getMatchingProfile(pageable);
        return ResponseEntity.ok(matchingResponse);
    }

    @Operation(summary = "공인 중개사 상세 프로필")
    @GetMapping(value = "/profile/detail/{agentId}")
    public ResponseEntity<DetailProfileResponse> getAgentDetailProfile(
            @Parameter(name = "agentId", description = "중개사 상세 프로필에 대한 아이디")
            @PathVariable(value = "agentId") String agentId
    ) {
        DetailProfileResponse detailProfileResponse = mainPageService.getAgentDetailProfile(agentId);
        return ResponseEntity.ok(detailProfileResponse);
    }

    @Operation(summary = "공인 중개사 포트폴리오")
    @GetMapping(value = "/portfolio/{agentId}")
    public ResponseEntity<PortfolioPageResponse> getAgentPortfolio(
            @Parameter(name = "agentId", description = "중개사 상세 프로필에 대한 아이디")
            @PathVariable(value = "agentId") String agentId,
            @Parameter(name = "page", description = "페이지 번호", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(name = "size", description = "페이지 크기", example = "9")
            @RequestParam(value = "size", defaultValue = "9") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PortfolioPageResponse portfolios = mainPageService.getAgentPortfolio(agentId, pageable);
        return ResponseEntity.ok(portfolios);
    }

    @Operation(summary = "공인 중개사 포트폴리오")
    @GetMapping(value = "/portfolio/{portfolioId}/detail")
    public ResponseEntity<DetailPortfolioResponse> getAgentPortfolioDetail(
            @Parameter(name = "portfolioId", description = "중개사 상세 프로필에 대한 아이디")
            @PathVariable(value = "portfolioId") Long portfolioId
    ) {
        DetailPortfolioResponse detailPortfolioResponse = mainPageService.getAgentPortfolioDetail(portfolioId);
        return ResponseEntity.ok(detailPortfolioResponse);
    }

    @Operation(summary = "공인 중개사 매칭 카테고리별 조회")
    @GetMapping(value = "/matching/category")
    public ResponseEntity<MatchingResponse> getAgentMatchingCategory(
            @Parameter(name = "category", description = "공인 중개사 카테고리")
            @RequestParam(value = "category") String category,
            @Parameter(name = "page", description = "페이지 번호", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(name = "size", description = "페이지 크기", example = "9")
            @RequestParam(value = "size", defaultValue = "9") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        MatchingResponse matchingResponse = mainPageService.getMatchingCategory(category, pageable);
        return ResponseEntity.ok(matchingResponse);
    }
}
