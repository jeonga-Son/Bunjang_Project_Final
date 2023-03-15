package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.POST_SHOP_NOT_EXISTS;

@Service
public class ReviewProvider {

    private final ReviewDao reviewDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.jwtService = jwtService;
    }


    public GetReviewsRes getReviewsRes(int userIdx) throws BaseException {
        // validation : 존재하는 상점(유저)인가?
        if (checkUserExists(userIdx) == 0)
            throw new BaseException(POST_SHOP_NOT_EXISTS);
        try {
            GetReviewsRes getReviewsRes = reviewDao.getReviews(userIdx);
            return getReviewsRes;
        } catch (Exception exception) {
            logger.error("App - getReviewsRes Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExists(int userIdx) throws BaseException {
        try {
            int result = reviewDao.checkUserExists(userIdx);
            return result;
        } catch (Exception exception) {
            logger.error("App - checkUserExists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
