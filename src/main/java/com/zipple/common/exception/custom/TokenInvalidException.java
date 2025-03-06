package com.zipple.common.exception.custom;

import com.zipple.common.exception.ApiException;
import com.zipple.common.exception.ErrorCode;

public class TokenInvalidException extends ApiException {
    public TokenInvalidException() {
        super(ErrorCode.TOKEN_INVALID);
    }
}
