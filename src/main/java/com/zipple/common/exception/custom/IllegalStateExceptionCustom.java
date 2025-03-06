package com.zipple.common.exception.custom;

import com.zipple.common.exception.ApiException;
import com.zipple.common.exception.ErrorCode;

public class IllegalStateExceptionCustom extends ApiException {
    public IllegalStateExceptionCustom() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
