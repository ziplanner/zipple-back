package com.zipple.common.exception.custom;

import com.zipple.common.exception.ApiException;
import com.zipple.common.exception.ErrorCode;

public class BadRequestException extends ApiException {
    public BadRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}
