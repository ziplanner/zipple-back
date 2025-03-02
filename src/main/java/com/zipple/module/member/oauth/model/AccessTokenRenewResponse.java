package com.zipple.module.member.oauth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "액세스 토큰 갱신 응답 데이터")
public class AccessTokenRenewResponse {
    private String accessToken;
    private Boolean isLogout;
}
