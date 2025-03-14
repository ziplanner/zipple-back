package com.zipple.common.sens.domain;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Hidden
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsSendRequest {
    private String phoneNumber;
}
