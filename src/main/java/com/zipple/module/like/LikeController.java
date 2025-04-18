package com.zipple.module.like;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            @PathVariable(value = "agentId") String agentId
    ) {
        likeService.likeAgent(agentId);
        return ResponseEntity.ok("좋아요가 추가되었습니다.");
    }

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/{agentId}")
    public ResponseEntity<String> unlikeAgent(
            @Parameter(name = "agentId", description = "좋아요 취소 받는 중개사")
            @PathVariable(value = "agentId") String agentId
    ) {
        likeService.unlikeAgent(agentId);
        return ResponseEntity.ok("좋아요가 취소되었습니다.");
    }

    @Operation(summary = "좋아요", description = "첫 클릭시 데이터 삽입 이후 누름, 안누름 반복")
    @PostMapping(value = "/once/{agentId}")
    public ResponseEntity<Void> onceLike(
            @Parameter(name = "agentId", description = "좋아요 취소 받는 중개사")
            @PathVariable(value = "agentId") String agentId
    ) {
        likeService.onceLikeAgent(agentId);
        return ResponseEntity.noContent().build();
    }
}
