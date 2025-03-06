package com.zipple.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "4001", "잘못된 요청입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "4002", "필수 요청 값이 누락되었습니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "4003", "입력 형식이 올바르지 않습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "4011", "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "4012", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "4013", "유효하지 않은 토큰입니다."),

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "4031", "접근 권한이 없습니다."),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "4041", "요청한 리소스를 찾을 수 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
