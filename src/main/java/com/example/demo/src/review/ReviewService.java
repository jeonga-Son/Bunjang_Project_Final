package com.example.demo.src.review;


import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ReviewService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final ReviewProvider reviewProvider;
    private final JwtService jwtService;


    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewProvider reviewProvider, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;

    }

    // 리뷰 쓰기 메서드
    public int createReview(int productIdx, PostReviewReq postReviewReq) throws BaseException {
        try {
            int userIdx = postReviewReq.getUserIdx();
            int reviewIdx = reviewDao.insertReview(productIdx, userIdx, postReviewReq);

            return reviewIdx;

        } catch (Exception exception) {
            logger.error("App - createReview Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
