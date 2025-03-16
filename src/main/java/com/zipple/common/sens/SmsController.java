package com.zipple.common.sens;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zipple.common.sens.domain.MessageRequest;
import com.zipple.common.sens.domain.SmsVerificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Tag(name = "문자 인증")
@RestController
@RequestMapping(value = "/api/v1/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @Operation(summary = "해당 번호로 인증 문자 보내기")
    @PostMapping(value = "/send")
    public ResponseEntity<String> sendSms(@RequestBody MessageRequest messageResponse) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, JsonProcessingException, InvalidKeyException {
        smsService.sendSms(messageResponse);
        return ResponseEntity.ok("문자 발송 완료");
    }

    @Operation(summary = "해당 번호로 인증 번호 인증하기")
    @PostMapping(value = "/verify")
    public ResponseEntity<String> verifyMessageCode(@RequestBody SmsVerificationRequest smsVerificationRequest) throws Exception {
        String result = smsService.verifyMessageCode(smsVerificationRequest);
        return ResponseEntity.ok(result);
    }
}