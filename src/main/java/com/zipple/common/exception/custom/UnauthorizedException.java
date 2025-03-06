package com.zipple.common.exception.custom;

import com.zipple.common.exception.ApiException;
import com.zipple.common.exception.ErrorCode;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
