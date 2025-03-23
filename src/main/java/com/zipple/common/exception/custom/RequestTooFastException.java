package com.zipple.common.exception.custom;

import com.zipple.common.exception.ApiException;
import com.zipple.common.exception.ErrorCode;

public class RequestTooFastException extends ApiException {
  public RequestTooFastException() {
    super(ErrorCode.REQUEST_TOO_FAST);
  }
}
