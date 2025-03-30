package com.zipple.module.mypage.agent;

import com.zipple.common.utils.GetMember;
import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import com.zipple.module.member.common.entity.category.AgentSpecialty;
import com.zipple.module.member.common.entity.category.AgentType;
import com.zipple.module.member.common.repository.AgentUserRepository;
import com.zipple.module.member.common.repository.UserRepository;
import com.zipple.module.mypage.agent.domain.BasicUpdateRequest;
import com.zipple.module.mypage.agent.domain.DetailUpdateRequest;
import com.zipple.module.mypage.agent.domain.MyPageAgentResponse;
import com.zipple.module.mypage.agent.portfolio.Portfolio;
import com.zipple.module.mypage.agent.portfolio.PortfolioImage;
import com.zipple.module.mypage.agent.portfolio.PortfolioImageRepository;
import com.zipple.module.mypage.agent.portfolio.PortfolioRepository;
import com.zipple.module.mypage.agent.portfolio.domain.MyPageAgentAllResponse;
import com.zipple.module.mypage.agent.portfolio.domain.PortfolioImageList;
import com.zipple.module.mypage.agent.portfolio.domain.PortfolioMainImage;
import com.zipple.module.mypage.agent.portfolio.domain.PortfolioPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageAgentService {

    private final GetMember getMember;
    private final AgentUserRepository agentUserRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioImageRepository portfolioImageRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Transactional(readOnly = true)
    public MyPageAgentResponse getLicensedAgent() {
        User currentUser = getMember.getCurrentMember();

        AgentUser agentUser = currentUser.getAgentUser();
        if (agentUser == null) {
            throw new IllegalArgumentException("Agent 정보가 없습니다.");
        }

        return MyPageAgentResponse.builder()
                .agentName(agentUser.getAgentName())
                .ownerName(agentUser.getOwnerName())
                .agentType(agentUser.getAgentType().getDescription())
                .phoneNumber(agentUser.getPrimaryContactNumber())
                .email(currentUser.getEmail())
                .createAt(currentUser.getUpdatedAt().toString())
                .build();
    }

    @Transactional
    public void updateLicensedAgent(BasicUpdateRequest request) {
        User currentUser = getMember.getCurrentMember();

        AgentUser agentUser = agentUserRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("AgentUser 정보를 찾을 수 없습니다."));

        agentUser.setPrimaryContactNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : agentUser.getPrimaryContactNumber());
        agentUser.setIntroductionTitle(request.getTitle() != null ? request.getTitle() : agentUser.getIntroductionTitle());
        agentUser.setIntroductionContent(request.getContent() != null ? request.getContent() : agentUser.getIntroductionContent());
        agentUser.setExternalLink(request.getExternalLink() != null ? request.getExternalLink() : agentUser.getExternalLink());

        currentUser.setEmail(request.getEmail());
        userRepository.save(currentUser);
        agentUserRepository.save(agentUser);
    }

    @Transactional
    public void updateLicensedAgentDetail(DetailUpdateRequest request) {
        User currentUser = getMember.getCurrentMember();

        AgentUser agentUser = agentUserRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("AgentUser 정보를 찾을 수 없습니다."));

        agentUser.setBusinessName(request.getBusinessName() != null ? request.getBusinessName() : agentUser.getBusinessName());
        agentUser.setAgentRegistrationNumber(request.getAgentRegistrationNumber() != null ? request.getAgentRegistrationNumber() : agentUser.getAgentRegistrationNumber());
        agentUser.setPrimaryContactNumber(request.getPrimaryContactNumber() != null ? request.getPrimaryContactNumber() : agentUser.getPrimaryContactNumber());
        agentUser.setOfficeAddress(request.getOfficeAddress() != null ? request.getOfficeAddress() : agentUser.getOfficeAddress());
        agentUser.setSingleHouseholdExpertRequest(request.getSingleHouseholdExpertRequest());

        agentUserRepository.save(agentUser);
    }

    @Transactional
    public void createLicensedPortfolio(PortfolioImageList portfolioImages, String portfolioTitle, String portfolioContent) {
        User currentUser = getMember.getCurrentMember();
        AgentUser agentUser = currentUser.getAgentUser();
        AgentType agentType = agentUser.getAgentType();
        Portfolio portfolio = Portfolio.builder()
                .user(currentUser)
                .title(portfolioTitle)
                .agentType(agentType)
                .content(portfolioContent)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<PortfolioImage> savedImages = new ArrayList<>();
        String baseDir = "/home/ubuntu/zipple/upload/";
        String baseUrl = "https://api.zipple.co.kr";

        for (int i = 0; i < portfolioImages.getPortfolioImages().size(); i++) {
            MultipartFile image = portfolioImages.getPortfolioImages().get(i);
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String filePath = baseDir + "/" + fileName;

            try {
                File destFile = new File(filePath);
                image.transferTo(destFile);

                String imageUrl = baseUrl + "/zipple" + baseDir + fileName;

                PortfolioImage portfolioImage = PortfolioImage.builder()
                        .portfolio(portfolio)
                        .imageUrl(imageUrl)
                        .isMain(i == 0)
                        .createdAt(LocalDateTime.now())
                        .build();

                savedImages.add(portfolioImage);

            } catch (IOException e) {
                throw new IllegalStateException("이미지 저장 중 오류 발생: " + image.getOriginalFilename(), e);
            }
        }

        portfolio.setPortfolioImage(savedImages);
        portfolioRepository.save(portfolio);
    }

//    @Transactional(readOnly = true)
//    public PortfolioPageResponse getLicensedAgentPortfolios(Pageable pageable) {
//        User user = getMember.getCurrentMember();
//        Long userId = user.getId();
//        AgentUser agentUser = agentUserRepository.findByUserId(userId);
//        Page<PortfolioMainImage> page = portfolioImageRepository.findByPortfolioUserIdAndPortfolioAgentTypeAndIsMainTrue(userId, pageable, agentUser.getAgentType());
//
//        return PortfolioPageResponse.builder()
//                .content(page.getContent())
//                .totalElements(page.getTotalElements())
//                .totalPages(page.getTotalPages())
//                .currentPage(page.getNumber())
//                .isLast(page.isLast())
//                .build();
//    }

    public PortfolioPageResponse getLicensedAgentPortfolios(Pageable pageable) {
        User user = getMember.getCurrentMember();
        Long userId = user.getId();
        AgentUser agentUser = agentUserRepository.findByUserId(userId);

        Page<PortfolioImage> portfolioPage = portfolioImageRepository.findByPortfolioUserIdAndPortfolioAgentTypeAndIsMainTrue(
                userId, agentUser.getAgentType(), pageable
        );

        List<PortfolioMainImage> portfolioList = portfolioPage.getContent().stream()
                .map(portfolioImage -> PortfolioMainImage.builder()
                        .portfolioId(portfolioImage.getPortfolio().getId())
                        .portfolioTitle(portfolioImage.getPortfolio().getTitle())
                        .portfolioContent(portfolioImage.getPortfolio().getContent())
                        .mainImageUrl(portfolioImage.getImageUrl())
                        .createdAt(portfolioImage.getPortfolio().getCreatedAt().format(DATE_FORMATTER))
                        .build())
                .collect(Collectors.toList());

        return PortfolioPageResponse.builder()
                .content(portfolioList)
                .totalElements(portfolioPage.getTotalElements())
                .totalPages(portfolioPage.getTotalPages())
                .currentPage(portfolioPage.getNumber())
                .isLast(portfolioPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageAgentAllResponse getAgentAllInfo() {
        return null;
    }

    @Transactional
    public void deleteLicensedAgentPortfolio(Long portfolioId) throws AccessDeniedException {
        User user = getMember.getCurrentMember();
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 포트폴리오가 존재하지 않습니다. ID = " + portfolioId));

        if (!portfolio.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인 소유의 포트폴리오만 삭제할 수 있습니다.");
        }

        portfolioRepository.delete(portfolio);
    }

    @Transactional
    public void updateLicensedAgentPortfolio(Long portfolioId, String portfolioTitle, String portfolioContent, PortfolioImageList portfolioImages) throws AccessDeniedException {
        User currentUser = getMember.getCurrentMember();
        AgentUser agentUser = currentUser.getAgentUser();
        AgentType agentType = agentUser.getAgentType();

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다."));

        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("본인의 포트폴리오만 수정할 수 있습니다.");
        }

        List<PortfolioImage> originalImages = portfolio.getPortfolioImage();
        originalImages.clear();

        String baseDir = "/home/ubuntu/zipple/upload/";
        String baseUrl = "https://api.zipple.co.kr";

        for (int i = 0; i < portfolioImages.getPortfolioImages().size(); i++) {
            MultipartFile image = portfolioImages.getPortfolioImages().get(i);
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String filePath = baseDir + "/" + fileName;

            try {
                File destFile = new File(filePath);
                image.transferTo(destFile);

                String imageUrl = baseUrl + "/zipple" + baseDir + fileName;

                PortfolioImage portfolioImage = PortfolioImage.builder()
                        .portfolio(portfolio)
                        .imageUrl(imageUrl)
                        .isMain(i == 0)
                        .createdAt(LocalDateTime.now())
                        .build();

                originalImages.add(portfolioImage);

            } catch (IOException e) {
                throw new IllegalStateException("이미지 저장 중 오류 발생: " + image.getOriginalFilename(), e);
            }
        }

        portfolio.setTitle(portfolioTitle);
        portfolio.setContent(portfolioContent);
        portfolio.setAgentType(agentType);
        portfolio.setUpdatedAt(LocalDateTime.now());

        portfolioRepository.save(portfolio);
    }
}

