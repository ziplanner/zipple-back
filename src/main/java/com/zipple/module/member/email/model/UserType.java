package com.zipple.module.member.email.model;

import lombok.Getter;

@Getter
public enum UserType {
    GENERAL("일반"),
    BUSINESS_AGENT("개업"),
    AFFILIATED_AGENT("소속"),
    NOT_REGISTERED("미등록");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public static UserType fromKorean(String korean) {
        if (korean == null || korean.trim().isEmpty()) {
            return UserType.NOT_REGISTERED;
        }
        for (UserType type : UserType.values()) {
            if (type.description.equals(korean.trim())) {
                return type;
            }
        }
        return UserType.NOT_REGISTERED;
    }
}
