package com.zipple.common.sens.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "메세지 인증 요청 데이터")
public class MessageRequest {
    @Schema(description = "폰 번호", example = "01023122132")
    private String to;

    @Hidden
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String content;
}
