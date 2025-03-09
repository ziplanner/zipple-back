package com.zipple.module.review;

import com.zipple.common.utils.GetMember;
import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import com.zipple.module.member.common.repository.AgentUserRepository;
import com.zipple.module.review.domain.ReviewRequest;
import com.zipple.module.review.domain.ReviewResponse;
import com.zipple.module.review.entity.Review;
import com.zipple.module.review.entity.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AgentUserRepository agentUserRepository;
    private final GetMember getMember;

    @Transactional
    public Review createReview(Long agentId, ReviewRequest reviewRequest) {
        User user = getMember.getCurrentMember();
        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        String content = reviewRequest.getContent();
        Integer starCount = reviewRequest.getStarCount();

        Review review = Review.builder()
                .user(user)
                .agentUser(agentUser)
                .content(content)
                .starCount(starCount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(Long reviewId, ReviewRequest reviewRequest) {
        User user = getMember.getCurrentMember();
        Review review = reviewRepository.findByIdAndUserId(reviewId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없거나 수정 권한이 없습니다."));


        String content = reviewRequest.getContent();
        Integer starCount = reviewRequest.getStarCount();

        review.updateReview(content, starCount);
        return review;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        User user = getMember.getCurrentMember();
        Review review = reviewRepository.findByIdAndUserId(reviewId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없거나 삭제 권한이 없습니다."));

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByAgent(Long agentId) {
        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        return reviewRepository.findByAgentUser(agentUser).stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getId())
                        .profileUrl(review.getUser().getProfile_image_url())
                        .nickname(review.getUser().getNickname())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt().toString())
                        .updatedAt(review.getUpdatedAt().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public double getAverageStarCount(Long agentId) {
        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        return 0.0;
    }
}
