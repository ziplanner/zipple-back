package com.zipple.common.sens;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@RestController
@RequestMapping(value = "/api/v1/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping(value = "/send")
    public ResponseEntity<String> sendSms(@RequestBody MessageResponse messageResponse) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, JsonProcessingException, InvalidKeyException {
        smsService.sendSms(messageResponse);
        return ResponseEntity.ok("문자 발송 완료");
    }
