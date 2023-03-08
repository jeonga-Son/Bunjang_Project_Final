package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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


    // 팔로잉 조회 메서드
//    public List<GetFollowingsRes> getFollowings(int userIdx) throws BaseException {
//        try {
//            List<GetFollowingsRes> getFollowingsRes = reviewDao.getFollowings(userIdx);
//            return getFollowingsRes;
//        } catch (Exception exception) {
//            logger.error("App - getFollowings Provider Error", exception);
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }



}
