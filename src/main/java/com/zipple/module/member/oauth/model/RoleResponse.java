package com.zipple.module.member.oauth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    @Schema(description = "유저 아이디", example = "1")
    private String userId;

    @Schema(description = "일반/개업/소속/미등록", example = "일반, 개업, 소속, 미등록")
    private String roleName;

    @Schema(description = "닉네임", example = "권수연")
    private String nickname;

    @Schema(description = "프로필 이미지 url", example = "https://api.zipple.co.kr/zipple/home/ubuntu/zipple/logo/zipplelogo.png")
    private String profileUrl;
}
