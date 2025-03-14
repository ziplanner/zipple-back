package com.zipple.common.sens.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문자 인증 확인 요청 데이터")
public class SmsVerificationRequest {

    @Schema(description = "폰 번호", example = "01022113231")
    private String phoneNumber;

    @Schema(description = "인증 번호", example = "103245")
    private String code;
}
