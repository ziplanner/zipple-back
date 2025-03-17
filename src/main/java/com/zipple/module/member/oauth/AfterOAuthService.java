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

@Slf4j
@Service
@RequiredArgsConstructor
public class AfterOAuthService {
    private final String URL = "https://api.zipple.co.kr/zipple/";
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
                .agentOfficeRegistrationCertificate(
                        URL + documentsPath(agentCertificationDocuments, 0)
                )
                .businessRegistrationCertification(
                        URL + documentsPath(agentCertificationDocuments, 1)
                )
                .agentLicense(URL + documentsPath(agentCertificationDocuments, 2))
                .agentImage(URL + imagePath(agentImage))
                .externalLink(
                        (agentUserRequest.getExternalLink() != null) ? agentUserRequest.getExternalLink() : ""
                )
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

    private String documentsPath(List<MultipartFile> agentCertificationDocuments, int count) {
        int index = 0;
        for (MultipartFile file : agentCertificationDocuments) {
            if (index == count) {
                try {
                    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String baseDir = "/home/ubuntu/zipple/upload";
                    Long userId = getMember.getCurrentMember().getId();
                    String directoryPath = baseDir + "/" + userId + "/" + date;

                    Path directory = Paths.get(directoryPath);
                    if (!Files.exists(directory)) {
                        Files.createDirectories(directory);
                    }

                    String fileName = file.getOriginalFilename();
                    if (fileName == null || fileName.isEmpty()) {
                        fileName = "default_filename";
                    }
                    Path filePath = directory.resolve(fileName);

                    file.transferTo(filePath.toFile());

                    return directoryPath + "/" + fileName;
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            index++;
        }
        return "";
    }

    private String imagePath(MultipartFile agentCertificationDocuments) {
        return "";
    }


}
