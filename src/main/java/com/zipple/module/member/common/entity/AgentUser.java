package com.zipple.module.member.common.entity;

import com.zipple.module.like.entity.AgentLike;
import com.zipple.module.member.common.entity.category.AgentType;
import com.zipple.module.member.common.entity.category.AgentSpecialty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agent_users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentUser {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToMany(mappedBy = "agentUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AgentLike> receivedLikes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AgentType agentType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255)")
    private AgentSpecialty agentSpecialty;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "agent_registration_number")
    private String agentRegistrationNumber;

    @Column(name = "primary_contact_number")
    private String primaryContactNumber;

    @Column(name = "office_address")
    private String officeAddress;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_contact_number")
    private String ownerContactNumber;

    @Column(name = "agent_name")
    private String agentName;

    @Column(name = "agent_contact_number")
    private String agentContactNumber;

    @Column(name = "single_household_expert_request")
    private Boolean singleHouseholdExpertRequest;

    @Column(name = "agent_office_registration_certificate")
    private String agentOfficeRegistrationCertificate;

    @Column(name = "business_registration_certification")
    private String businessRegistrationCertification;

    @Column(name = "agent_license")
    private String agentLicense;

    @Column(name = "agent_image")
    private String agentImage;

    @Column(name = "introduction_title")
    private String introductionTitle;

    @Column(name = "introduction_content")
    private String introductionContent;

    @Column(name = "external_link")
    private String externalLink;

    @Column(name = "mandatory_terms")
    private Boolean mandatoryTerms;

    @Column(name = "optional_terms")
    private Boolean optionalTerms;
}
