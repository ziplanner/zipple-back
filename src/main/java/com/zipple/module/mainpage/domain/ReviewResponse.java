package com.zipple.module.mainpage.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private String userId;
    private String nickname;
    private String profileUrl;
    private Integer startCount;
    private String comments;

}
