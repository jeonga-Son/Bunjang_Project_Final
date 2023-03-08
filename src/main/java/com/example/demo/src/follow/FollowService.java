package com.example.demo.src.follow;


import com.example.demo.config.BaseException;
import com.example.demo.src.follow.*;
import com.example.demo.src.follow.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FollowService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FollowDao followDao;
    private final FollowProvider followProvider;
    private final JwtService jwtService;


    @Autowired
    public FollowService(FollowDao followDao, FollowProvider followProvider, JwtService jwtService) {
        this.followDao = followDao;
        this.followProvider = followProvider;
        this.jwtService = jwtService;

    }

    // 팔로우 하기 메서드
    public int followUser(int followerIdx, int followingUserIdx) throws BaseException {
        try {
            int followIdx = followDao.insertFollow(followerIdx, followingUserIdx);

            return followIdx;

        } catch (Exception exception) {
            logger.error("App - followUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 팔로우 취소 메서드
    public int unfollow(int followerIdx, int followingUserIdx) throws BaseException {
        try {
            int followIdx = followDao.getFollowIdx(followerIdx, followingUserIdx);
            int result = followDao.updateFollowStatus(followIdx);

            if (result == 1) return followIdx;
            else throw new BaseException(DATABASE_ERROR);
        } catch (Exception exception) {
            logger.error("App - followUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
