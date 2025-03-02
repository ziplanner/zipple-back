package com.zipple.module.review;

import com.zipple.module.mainpage.domain.ReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {


    public List<ReviewResponse> getReview(Long userId) {
        return null;
    }
}
