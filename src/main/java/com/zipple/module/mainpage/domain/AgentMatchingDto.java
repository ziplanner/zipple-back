package com.zipple.module.mainpage.domain;

import com.zipple.module.member.common.entity.category.AgentSpecialty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgentMatchingDto {
    private Long agentId;
    private String profileUrl;
    private String agentSpecialty;
    private String agentName;
    private String title;
    private int portfolioCount;
    private int likeCount;
    private int reviewCount;
    private int starRating;
    private Boolean singleHouseholdExpertRequest;
}
