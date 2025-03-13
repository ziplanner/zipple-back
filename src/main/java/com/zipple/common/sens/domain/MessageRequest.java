package com.zipple.common.sens.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "메세지 인증 요청 데이터")
public class MessageRequest {

    @Schema(description = "인증 문자 받을 번호", example = "01024321111")
    private String phoneNumber;
}
