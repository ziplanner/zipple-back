package com.zipple.module.like;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "좋아요")
public class LikeController {

    private final LikeService likeService;

    @Operation(
            summary = "좋아요"
    )
    @GetMapping(value = "/like")
    public ResponseEntity<Void> getLike() {
        return ResponseEntity.ok().build();
    }
}
