package com.zipple.common.exception.custom;

import com.zipple.common.exception.ApiException;
import com.zipple.common.exception.ErrorCode;

public class ForbiddenException extends ApiException {
    public ForbiddenException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
