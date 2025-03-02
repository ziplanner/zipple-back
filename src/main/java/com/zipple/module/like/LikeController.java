package com.zipple.module.like;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/like")
@Tag(name = "좋아요")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 추가")
    @PostMapping("/{agentId}")
    public ResponseEntity<String> likeAgent(
            @Parameter(name = "agentId", description = "좋아요 추가 받는 중개사")
            @PathVariable(value = "agentId") Long agentId
    ) {
        likeService.likeAgent(agentId);
        return ResponseEntity.ok("좋아요가 추가되었습니다.");
    }

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/{agentId}")
    public ResponseEntity<String> unlikeAgent(
            @Parameter(name = "agentId", description = "좋아요 취소 받는 중개사")
            @PathVariable(value = "agentId") Long agentId
    ) {
        likeService.unlikeAgent(agentId);
        return ResponseEntity.ok("좋아요가 취소되었습니다.");
    }

    @Operation(summary = "좋아요 갯수")
    @GetMapping("/{agentId}/count")
    public ResponseEntity<Long> getAgentLikeCount(
            @Parameter(name = "agentId", description = "좋아요 갯수")
            @PathVariable(value = "agentId") Long agentId
    ) {
        return ResponseEntity.ok(likeService.getAgentLikeCount(agentId));
    }
}
