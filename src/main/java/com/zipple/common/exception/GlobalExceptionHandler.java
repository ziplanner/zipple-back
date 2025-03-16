//package com.zipple.common.exception;
//
//import com.zipple.common.exception.custom.TokenInvalidException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    private ResponseEntity<Object> buildResponse(ErrorCode errorCode) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", errorCode.getStatus().value());
//        response.put("errorCode", errorCode.getCode());
//        response.put("message", errorCode.getMessage());
//        return new ResponseEntity<>(response, errorCode.getStatus());
//    }
//
//    @ExceptionHandler(ApiException.class)
//    public ResponseEntity<Object> handleApiException(ApiException ex) {
//        return buildResponse(ex.getErrorCode());
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleGenericException(Exception ex) {
//        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
//    }
//}
