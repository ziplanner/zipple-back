package com.zipple.common.sens;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipple.common.sens.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${sens.Access-Key-ID}")
    private String accessKey;

    @Value("${sens.Secret-Key}")
    private String secretKey;

    @Value("${sens.Service-ID}")
    private String serviceId;

    @Value("${sens.Sender-Phone}")
    private String phoneNumber;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private final String PREFIX = "sms:verification:";
    private final long EXPIRATION_TIME = 180;

    public void sendSms(MessageRequest messageResponse) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, java.security.InvalidKeyException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        String messageCode = generateRandomCode();
        String messageContent = "[" + messageCode + "] 집플에서 보내는 인증번호 입니다.";

        String sendNumber = messageResponse.getTo();
        List<MessageRequest> messages = new ArrayList<>();
        messages.add(messageResponse);

        saveVerificationCode(sendNumber, messageCode);

        SmsRequest request = SmsRequest.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(phoneNumber)
                .content(messageContent)
                .messages(messages)
                .build();

        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsResponse response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponse.class);

    }

    private String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, java.security.InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void saveVerificationCode(String phoneNumber, String code) {
        String key = PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(key, code, EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public String getVerificationCode(String phoneNumber) {
        return redisTemplate.opsForValue().get(PREFIX + phoneNumber);
    }

    public void deleteVerificationCode(String phoneNumber) {
        redisTemplate.delete(PREFIX + phoneNumber);
    }

    public String verifyMessageCode(SmsVerificationRequest smsVerificationRequest) {
        String result;
        String phoneNumber = smsVerificationRequest.getPhoneNumber();
        String code = smsVerificationRequest.getCode();

        String redisCode = getVerificationCode(phoneNumber);
        if(code.equals(redisCode)) {
            result = "인증에 성공하셨습니다.";
            deleteVerificationCode(phoneNumber);
        } else {
            result = "인증 번호가 일치하지 않습니다.";
        }
        return result;
    }
}
