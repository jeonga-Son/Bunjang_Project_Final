package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.src.follow.model.GetFollowersRes;
import com.example.demo.src.follow.model.GetFollowingsRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.POST_SHOP_NOT_EXISTS;

@Service
public class FollowProvider {

    private final FollowDao followDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public FollowProvider(FollowDao followDao, JwtService jwtService) {
        this.followDao = followDao;
        this.jwtService = jwtService;
    }


    // 팔로잉 조회 메서드
    public List<GetFollowingsRes> getFollowings(int userIdx) throws BaseException {
        // validation : 존재하는 유저(상점)인지?
        if(checkUserExists(userIdx) == 0)
            throw new BaseException(POST_SHOP_NOT_EXISTS);

        try {
            List<GetFollowingsRes> getFollowingsRes = followDao.getFollowings(userIdx);
            return getFollowingsRes;
        } catch (Exception exception) {
            logger.error("App - getFollowings Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 팔로워 조회 메서드
    public List<GetFollowersRes> getFollowers(int userIdx) throws BaseException {
        // validation : 존재하는 유저(상점)인지?
        if(checkUserExists(userIdx) == 0)
            throw new BaseException(POST_SHOP_NOT_EXISTS);

        try {
            List<GetFollowersRes> getFollowersRes = followDao.getFollowers(userIdx);
            return getFollowersRes;
        } catch (Exception exception) {
            logger.error("App - getFollowers Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExists(int userIdx) throws BaseException {
        try {
            int result = followDao.checkUserExists(userIdx);
            return result;
        } catch (Exception exception) {
            logger.error("App - checkUserExists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
