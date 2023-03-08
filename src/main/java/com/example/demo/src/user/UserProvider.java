package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            logger.error("App - getUser Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetMyPageRes getMyPage(int userIdx) throws BaseException {
        try {
            GetMyPageRes getMyPageRes = userDao.getMyPage(userIdx);
            return getMyPageRes;
        } catch (Exception exception) {
            logger.error("App - getUser Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetShopRes getShop(int userIdx) {
        try {
            GetShopRes getShopRes = userDao.getShop(userIdx);
            return getShopRes;
        } catch (Exception exception) {
            logger.error("App - getShop Provider Error", exception);
        }
        return null;
    }

    public PostLoginRes logIn(PostUserReq postUserReq) throws BaseException {
        try {
            int resultIdx = userDao.checkPhoneNo(postUserReq);

            if(resultIdx == 1){
                int userIdx = postUserReq.getUserIdx();
                String jwt = jwtService.createJwt(postUserReq.getUserIdx());
                String name = postUserReq.getName();
                String resultMessage = "'" + name + "'" + "님 로그인에 성공하였습니다.";
                return new PostLoginRes(userIdx,name,jwt,resultMessage);
            }
            else{
                throw new BaseException(FAILED_TO_LOGIN);
            }
        } catch (Exception exception) {
            logger.error("App - logIn Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
