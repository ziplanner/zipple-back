package com.zipple.module.member.oauth;

import com.zipple.common.utils.GetMember;
import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.GeneralUser;
import com.zipple.module.member.common.entity.User;
import com.zipple.module.member.common.entity.category.AgentSpecialty;
import com.zipple.module.member.common.entity.category.AgentType;
import com.zipple.module.member.common.entity.category.HousingType;
import com.zipple.module.member.common.repository.UserRepository;
import com.zipple.module.member.oauth.model.AgentUserRequest;
import com.zipple.module.member.oauth.model.GeneralUserRequest;
import com.zipple.module.member.common.repository.AgentUserRepository;
import com.zipple.module.member.common.repository.GeneralUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AfterOAuthService {
    private final String URL = "https://api.zipple.co.kr/zipple";
    private final GetMember getMember;
    private final GeneralUserRepository generalUserRepository;
    private final AgentUserRepository agentUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void generalRegister(GeneralUserRequest generalUserRequest) {
        GeneralUser generalUser = GeneralUser.builder()
                .user(getMember.getCurrentMember())
                .generalName(generalUserRequest.getGeneralName())
                .generalAddress(generalUserRequest.getGeneralAddress())
                .housingType(HousingType.fromValue(generalUserRequest.getHousingType()))
                .generalNumber(generalUserRequest.getGeneralNumber())
                .mandatoryTerms(generalUserRequest.getMarketingNotificationTerms() != null && generalUserRequest.getMarketingNotificationTerms())
                .optionalTerms(generalUserRequest.getMarketingNotificationTerms())
                .build();
        generalUserRepository.save(generalUser);
    }

    @Transactional
    public void agentRegisters(AgentUserRequest agentUserRequest, List<MultipartFile> agentCertificationDocuments, MultipartFile agentImage) {
        User user = getMember.getCurrentMember();
        AgentSpecialty agentSpecialty = AgentSpecialty.getByDescription(agentUserRequest.getAgentSpecialty());

        String cert1Path = saveDocumentFile(agentCertificationDocuments, 0);
        String cert2Path = saveDocumentFile(agentCertificationDocuments, 1);
        String cert3Path = saveDocumentFile(agentCertificationDocuments, 2);
        String imagePath = saveSingleFile(agentImage);

        AgentUser agentUser = AgentUser.builder()
                .user(user)
                .agentType(AgentType.fromValue(agentUserRequest.getAgentType()))
                .businessName(agentUserRequest.getBusinessName())
                .agentRegistrationNumber(agentUserRequest.getAgentRegistrationNumber())
                .primaryContactNumber(agentUserRequest.getPrimaryContactNumber())
                .officeAddress(agentUserRequest.getOfficeAddress())
                .ownerName(agentUserRequest.getOwnerName())
                .ownerContactNumber(agentUserRequest.getOwnerContactNumber())
                .singleHouseholdExpertRequest(agentUserRequest.getSingleHousehold())
                .agentOfficeRegistrationCertificate(URL + cert1Path)
                .businessRegistrationCertification(URL + cert2Path)
                .agentLicense(URL + cert3Path)
                .agentImage(URL + imagePath)
                .externalLink(agentUserRequest.getExternalLink() != null ? agentUserRequest.getExternalLink() : "")
                .introductionTitle(agentUserRequest.getIntroductionTitle())
                .introductionContent(agentUserRequest.getIntroductionContent())
                .agentName(agentUserRequest.getAgentName())
                .agentContactNumber(agentUserRequest.getAgentContactNumber())
                .agentSpecialty(agentSpecialty)
                .mandatoryTerms(true)
                .birthday(agentUserRequest.getBirthday())
                .foreigner(agentUserRequest.getForeigner())
                .messageVerify(agentUserRequest.getMessageVerify())
                .optionalTerms(agentUserRequest.getMarketingAgree())
                .build();

        agentUserRepository.save(agentUser);

        user.setEmail(agentUserRequest.getEmail());
        user.setAgentUser(agentUser);
        userRepository.save(user);
    }

    private String saveDocumentFile(List<MultipartFile> files, int index) {
        if (files == null || files.size() <= index || files.get(index).isEmpty()) return "";

        return saveToDisk(files.get(index));
    }

    private String saveSingleFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return "";
        return saveToDisk(file);
    }

    private String saveToDisk(MultipartFile file) {
        try {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Long userId = getMember.getCurrentMember().getId();
            String baseDir = "/home/ubuntu/zipple/upload/" + userId + "/" + date;

            Files.createDirectories(Paths.get(baseDir));

            String fileName = UUID.randomUUID() + "_" + Optional.ofNullable(file.getOriginalFilename()).orElse("default.file");
            Path fullPath = Paths.get(baseDir, fileName);

            file.transferTo(fullPath);

            return userId + "/" + date + "/" + fileName;
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage(), e);
            return "";
        }
    }
}
