package com.zipple.module.mainpage;

import com.zipple.common.utils.AgentIdBase64Util;
import com.zipple.module.like.entity.AgentLikeRepository;
import com.zipple.module.mainpage.domain.*;
import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import com.zipple.module.member.common.entity.category.AgentSpecialty;
import com.zipple.module.member.common.entity.category.AgentType;
import com.zipple.module.member.common.repository.AgentUserRepository;
import com.zipple.module.member.common.repository.UserRepository;
import com.zipple.module.mypage.agent.portfolio.Portfolio;
import com.zipple.module.mypage.agent.portfolio.PortfolioImage;
import com.zipple.module.mypage.agent.portfolio.PortfolioImageRepository;
import com.zipple.module.mypage.agent.portfolio.PortfolioRepository;
import com.zipple.module.mypage.agent.portfolio.domain.PortfolioMainImage;
import com.zipple.module.mypage.agent.portfolio.domain.PortfolioPageResponse;
import com.zipple.module.mypage.agent.portfolio.domain.PortfolioProfile;
import com.zipple.module.review.domain.ReviewResponse;
import com.zipple.module.review.entity.Review;
import com.zipple.module.review.entity.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainPageService {

    private final AgentUserRepository agentUserRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final UserRepository userRepository;
    private final AgentLikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final AgentIdBase64Util agentIdUUIDUtil;
    private final AgentLikeRepository agentLikeRepository;

    @Transactional(readOnly = true)
    public MatchingResponse getMatchingProfile(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> users = userPage.getContent();
        List<AgentMatchingResponse> matchingList = new ArrayList<>();

        for(User user : users) {
            Long userId = user.getId();
            int portfolioCount = portfolioRepository.countByUserId(userId);
            Optional<AgentUser> optionalAgentUser = agentUserRepository.findById(userId);

            if (optionalAgentUser.isPresent()) {
                AgentUser agentUser = optionalAgentUser.get();
                String agentSpecialty = AgentSpecialty.getDescriptionByAgentSpecialty(agentUser.getAgentSpecialty());

                Integer likeCount = likeRepository.countByAgentUserId(userId, false);
                Integer reviewCount = reviewRepository.countByAgentUser(agentUser);
                Double startRating = reviewRepository.findAverageStarCountByAgent(agentUser.getId());
                AgentMatchingResponse agentMatchingResponse = AgentMatchingResponse.builder()
                        .agentId(agentIdUUIDUtil.encodeLong(userId))
                        .profileUrl(user.getProfile_image_url())
                        .agentSpecialty(agentSpecialty)
                        .portfolioCount(portfolioCount)
                        .agentName(agentUser.getAgentName())
                        .title(agentUser.getIntroductionTitle())
                        .starRating(startRating == null ? 0.0 : startRating)
                        .likeCount(likeCount)
                        .reviewCount(reviewCount)
                        .singleHouseholdExpert(agentUser.getSingleHouseholdExpertRequest())
                        .build();
                matchingList.add(agentMatchingResponse);
            }
        }

        return MatchingResponse.builder()
                .matching(matchingList)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(userPage.getNumber())
                .isLast(userPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public DetailProfileResponse getAgentDetailProfile(String agentId) {
        Long userId = agentIdUUIDUtil.decodeLong(agentId);
        AgentUser agentUser = agentUserRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        User user = agentUser.getUser();

        List<Portfolio> portfolios = portfolioRepository.findTop6ByUserIdOrderByCreatedAtDesc(userId);

        List<PortfolioProfile> portfolioProfiles = portfolios.stream()
                .map(portfolio -> {
                    String mainImageUrl = portfolio.getPortfolioImage().stream()
                            .filter(PortfolioImage::getIsMain)
                            .map(PortfolioImage::getImageUrl)
                            .findFirst()
                            .orElse("");

                    return PortfolioProfile.builder()
                            .portfolioId(portfolio.getId())
                            .title(portfolio.getTitle())
                            .createdAt(portfolio.getCreatedAt().toString())
                            .portfolioImage(mainImageUrl)
                            .build();
                })
                .collect(Collectors.toList());

        List<Review> reviews = reviewRepository.findTop6ByAgentUserIdOrderByCreatedAtDesc(agentUser.getId());

        List<ReviewResponse> reviewResponses = reviews.stream().map(review ->
                ReviewResponse.builder()
                        .reviewId(review.getId())
                        .profileUrl(Optional.ofNullable(review.getUser().getProfile_image_url()).orElse(""))
                        .nickname(Optional.ofNullable(review.getUser().getNickname()).orElse(""))
                        .starCount(review.getStarCount())
                        .content(Optional.ofNullable(review.getContent()).orElse(""))
                        .createdAt(review.getCreatedAt().toString())
                        .updatedAt(review.getUpdatedAt().toString())
                        .build()
        ).collect(Collectors.toList());


        Double startRating = Optional.ofNullable(reviewRepository.findAverageStarCountByAgent(agentUser.getId())).orElse(0.0);

        String agentType = AgentType.getDescriptionByAgentType(agentUser.getAgentType());
        Integer reviewTotalCount = reviewRepository.countAllByAgentUser(agentUser);

        return DetailProfileResponse.builder()
                .email(user.getEmail())
                .profileUrl(Optional.ofNullable(user.getProfile_image_url()).orElse(""))
                .title(user.getNickname())
                .externalLink(Optional.ofNullable(agentUser.getExternalLink()).orElse(""))
                .agentName(agentUser.getAgentName())
                .starRating(startRating)
                .reviewTotalCount(reviewTotalCount)
                .agentType(agentType)
                .landLineNumber(agentUser.getPrimaryContactNumber())
                .businessName(agentUser.getBusinessName())
                .agentSpecialty(AgentSpecialty.getDescriptionByAgentSpecialty(agentUser.getAgentSpecialty()))
                .agentRegistrationNumber(agentUser.getAgentRegistrationNumber())
                .ownerName(agentUser.getOwnerName())
                .ownerContactNumber(agentUser.getOwnerContactNumber())
                .officeAddress(agentUser.getOfficeAddress())
                .portfolios(portfolioProfiles)
                .reviews(reviewResponses)
                .build();
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public PortfolioPageResponse getAgentPortfolio(String agentId, Pageable pageable) {
        Long userId = agentIdUUIDUtil.decodeLong(agentId);
        AgentUser agentUser = agentUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        AgentType agentType = agentUser.getAgentType();

        Page<PortfolioImage> portfolioPage = portfolioImageRepository.findByPortfolioUserIdAndPortfolioAgentTypeAndIsMainTrue(
                userId, agentType, pageable
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

//    @Transactional(readOnly = true)
//    public PortfolioPageResponse getAgentPortfolio(String agentId, Pageable pageable) {
//        Long userId = agentIdUUIDUtil.decodeLong(agentId);
//        AgentUser agentUser = agentUserRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));
//
//        AgentType agentType = agentUser.getAgentType();
//
//        Page<PortfolioMainImage> page = portfolioImageRepository.findByPortfolioUserIdAndPortfolioAgentTypeAndIsMainTrue(userId, agentType, pageable);
//
//        return PortfolioPageResponse.builder()
//                .content(page.getContent())
//                .totalElements(page.getTotalElements())
//                .totalPages(page.getTotalPages())
//                .currentPage(page.getNumber())
//                .isLast(page.isLast())
//                .build();
//    }

    @Transactional(readOnly = true)
    public MatchingResponse getMatchingCategory(String category, Pageable pageable) {
        Page<User> userPage;

        if (category == null || category.trim().isEmpty()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findByAgentUser_AgentSpecialty(AgentSpecialty.getByDescription(category), pageable);
        }

        List<User> users = userPage.getContent();
        List<AgentMatchingResponse> matchingList = new ArrayList<>();

        for (User user : users) {
            Long userId = user.getId();
            int portfolioCount = portfolioRepository.countByUserId(userId);
            Optional<AgentUser> optionalAgentUser = agentUserRepository.findById(userId);

            if (optionalAgentUser.isPresent()) {
                AgentUser agentUser = optionalAgentUser.get();
                String agentSpecialty = AgentSpecialty.getDescriptionByAgentSpecialty(agentUser.getAgentSpecialty());
                Integer likeCount = likeRepository.countByAgentUserId(userId);
                Integer reviewCount = reviewRepository.countByAgentUser(agentUser);
                Double starRating = Optional.ofNullable(reviewRepository.findAverageStarCountByAgent(agentUser.getId())).orElse(0.0);


                boolean isLiked = agentLikeRepository.existsByUserIdAndAgentUserId(userId, agentUser.getId());
                AgentMatchingResponse agentMatchingResponse = AgentMatchingResponse.builder()
                        .agentId(agentIdUUIDUtil.encodeLong(userId))
                        .profileUrl(user.getProfile_image_url())
                        .agentSpecialty(agentSpecialty)
                        .portfolioCount(portfolioCount)
                        .agentName(agentUser.getAgentName())
                        .title(agentUser.getIntroductionTitle())
                        .starRating(starRating)
                        .businessName(agentUser.getBusinessName())
                        .likeCount(likeCount)
                        .liked(isLiked)
                        .reviewCount(reviewCount)
                        .singleHouseholdExpert(agentUser.getSingleHouseholdExpertRequest())
                        .build();
                matchingList.add(agentMatchingResponse);
            }
        }

        return MatchingResponse.builder()
                .matching(matchingList)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(userPage.getNumber())
                .isLast(userPage.isLast())
                .build();
    }

    public DetailPortfolioResponse getAgentPortfolioDetail(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdWithImages(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("포트폴리오를 찾을 수 없습니다. ID: " + portfolioId));

        List<String> imageUrls = portfolio.getPortfolioImage().stream()
                .sorted(Comparator.comparing(PortfolioImage::getIsMain).reversed())
                .map(image -> Optional.ofNullable(image.getImageUrl()).orElse(""))
                .toList();

        String externalLink = Optional.ofNullable(portfolio.getUser().getAgentUser())
                .map(AgentUser::getExternalLink)
                .orElse("");

        return DetailPortfolioResponse.builder()
                .agentId(agentIdUUIDUtil.encodeLong(portfolio.getUser().getId()))
                .title(portfolio.getTitle())
                .externalLink(externalLink)
                .content(Optional.ofNullable(portfolio.getContent()).orElse(""))
                .portfolioList(imageUrls)
                .build();
    }

    public ReviewPageResponse getAgentReview(String agentId, Pageable pageable) {
        Long userId = agentIdUUIDUtil.decodeLong(agentId);
        AgentUser agentUser = agentUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        Page<Review> reviewPage = reviewRepository.findPagedReviewsByAgentUser(agentUser, pageable);

        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(this::convertToReviewResponse)
                .toList();

        return ReviewPageResponse.builder()
                .content(reviewResponses)
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .currentPage(reviewPage.getNumber())
                .isLast(reviewPage.isLast())
                .build();
    }

    private ReviewResponse convertToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .profileUrl(review.getUser().getProfile_image_url())
                .nickname(review.getUser().getNickname())
                .starCount(review.getStarCount())
                .content(review.getContent())
                .createdAt(review.getCreatedAt().toString())
                .updatedAt(review.getUpdatedAt().toString())
                .build();
    }
}
