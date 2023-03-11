package com.example.demo.src.follow;


import com.example.demo.config.BaseException;
import com.example.demo.config.RichException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

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
    public int followUser(int followerIdx, int followingUserIdx) throws BaseException, RichException {
        try {

            int followIdx = 0;

            // validation : 이미 팔로우하고 있는지?
            if (getFollowIdx(followerIdx, followingUserIdx, "ACTIVE") != 0)
                throw new RichException(DUPLICATED_FOLLOW);

            // 이전에 팔로우 했다가 취소한 사람이면, 새로 추가하지 않고 followStatus 변경
            int getFollowIdxResult = getFollowIdx(followerIdx, followingUserIdx, "INACTIVE");
            if (getFollowIdxResult != 0) {
                followIdx = getFollowIdxResult;
                followDao.reFollow(followIdx);
            } else {
                followIdx = followDao.insertFollow(followerIdx, followingUserIdx);
            }

            return followIdx;

        } catch (RichException richException) {
            logger.error("App - followUser Service Error", richException);
            throw new RichException(richException.getStatus());
        }catch (Exception exception) {
            logger.error("App - followUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 팔로우 취소 메서드
    public int unfollow(int followerIdx, int followingUserIdx) throws BaseException, RichException {
        try {
            // validation : 존재하는 팔로우인가?
            int followIdx = getFollowIdx(followerIdx, followingUserIdx, "ACTIVE");
            if(followerIdx == 0)
                throw new RichException(FOLLOW_NOT_EXISTS);

//            // validation : 권한이 있는 유저인가?
//            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
//            if (getUserIdxOfFollow(followIdx) != userIdxByJwt)
//                throw new RichException(INVALID_USER_JWT);

            int result = followDao.updateFollowStatus(followIdx);

            if (result == 1)
                return followIdx;
            else
                throw new BaseException(DATABASE_ERROR);
        } catch (RichException richException) {
            logger.error("App - unfollow Service Error", richException);
            throw new RichException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - unfollow Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 팔로우 했던 이력이 있는 사람인지?
    public int getFollowIdx(int followerIdx, int followingUserIdx, String status) throws BaseException { // 존재하면 followIdx 출력, 없으면 0 출력
        try {
            int result = followDao.getFollowIdx(followerIdx, followingUserIdx, status);
            return result;
        } catch (Exception exception) {
            logger.error("App - getFollowIdx Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getFollowIdx(int followerIdx, int followingUserIdx) throws BaseException { // 존재하면 followIdx 출력, 없으면 0 출력
        try {
            int result = followDao.getFollowIdx(followerIdx, followingUserIdx);
            return result;
        } catch (Exception exception) {
            logger.error("App - getFollowIdx Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getUserIdxOfFollow(int followIdx) throws BaseException {
        try {
            int result = followDao.getUserIdxOfFollow(followIdx);

            return result;
        } catch (Exception exception) {
            logger.error("App - getUserIdxOfFollow Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
