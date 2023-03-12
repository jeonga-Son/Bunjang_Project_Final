package com.example.demo.src.review;


import com.example.demo.config.BaseException;
import com.example.demo.config.RichException;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

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
    public int createReview(int productIdx, PostReviewReq postReviewReq) throws BaseException, RichException {
        try {
            int userIdx = postReviewReq.getUserIdx();

            // 존재하는 상품인지?
            if(checkProductExists(productIdx) == 0)
                throw new RichException(PRODUCT_NOT_EXISTS);

            // validation : 판매 완료인 상품인지?
            String getProductStatusResult = getProductStatus(productIdx).toString();
            if(getProductStatusResult == "SOLD")
                throw new RichException(INVALID_PRODUCT_STATUS);

            // validation : 이미 작성된 리뷰가 있는지?
            if(isReviewWritten(productIdx) != 0)
                throw new RichException(DUPLICATED_PRODUCT_REVIEW);

            int reviewIdx = reviewDao.insertReview(productIdx, userIdx, postReviewReq);

            return reviewIdx;

        } catch (RichException richException) {
            logger.error("App - createReview Service Error", richException);
            throw new BaseException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - createReview Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 리뷰 삭제 메서드
    public int deleteReview(int reviewIdx) throws BaseException, RichException {
        try {
            // validation : 존재하는 리뷰인지?
            if (checkReviewExists(reviewIdx) == 0)
                throw new RichException(REVIEW_NOT_EXISTS);

            // validation : 권한이 있는 유저인지?
            int userIdxByJwt = jwtService.getUserIdx();
            if (getUserIdxOfReview(reviewIdx) != userIdxByJwt)
                throw new RichException(INVALID_USER_JWT);

            int result = reviewDao.updateReviewStatus(reviewIdx);

            if (result == 1)
                return reviewIdx;
            else
                throw new BaseException(DATABASE_ERROR);
        } catch (RichException richException) {
            logger.error("App - deleteReview Service Error", richException);
            throw new BaseException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - deleteReview Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewExists(int reviewIdx) throws BaseException {
        try{
            int result = reviewDao.checkReviewExists(reviewIdx);
            return result;
        } catch (Exception exception) {
            logger.error("App - checkReviewExists Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getUserIdxOfReview(int reviewIdx) throws BaseException {
        try {
            int result = reviewDao.getUserIdxOfReview(reviewIdx);
            return result;
        } catch (Exception exception) {
            logger.error("App - getUserIdxOfReview Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getProductStatus(int productIdx) throws BaseException {
        try {
            String result = reviewDao.getProductStatus(productIdx);
            return result;
        } catch (Exception exception) {
            logger.error("App - getProductStatus Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int isReviewWritten(int productIdx) throws BaseException {
        try {
            int result = reviewDao.isReviewWritten(productIdx);
            return result;
        } catch (Exception exception) {
            logger.error("App - isReviewWritten Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 존재하는 상품인지?
    public int checkProductExists(int productIdx) throws BaseException{
        try{
            return reviewDao.checkProductExists(productIdx);
        } catch (Exception exception){
            logger.error("App - checkProductExists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
