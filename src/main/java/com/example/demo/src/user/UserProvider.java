package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public GetStoreRes getStore(int userIdx) throws BaseException {

//        if (userDao.getUser(userIdx).equals(null)) {
//            throw new BaseException(POST_USERS_EMPTY_USER);
//        }

        try {
            GetStoreRes getStoreRes = userDao.getStore(userIdx);
            return getStoreRes;
        } catch (Exception exception) {
            logger.error("App - getShop Provider Error", exception);
        }
        return null;
    }

    public List<GetStoreProductsRes> getStoreProducts(int userIdx) throws BaseException {
//        if (userDao.getUser(userIdx).equals(null)) {
//            throw new BaseException(POST_USERS_EMPTY_USER);
//        }

        try {
            List<GetStoreProductsRes> getStoreProductsRes = userDao.getStoreProducts(userIdx);
            return getStoreProductsRes;
        } catch (Exception exception) {
            logger.error("App - getShop Provider Error", exception);
        }
        return null;
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        try {
            User user = userDao.checkUser(postLoginReq);

            if(!user.getPhoneNo().isEmpty()){
                int userIdx = user.getUserIdx();
                String name = user.getName();
                String jwt = jwtService.createJwt(userIdx);
                String resultMessage = "'" + name + "'" + "님 로그인에 성공하였습니다.";
                return new PostLoginRes(userIdx, name, jwt, resultMessage);
            }
            else{
                throw new BaseException(FAILED_TO_LOGIN);
            }
        } catch (Exception exception) {
            logger.error("App - logIn Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkPhoneNo(String phoneNo) throws BaseException {
        try{
            return userDao.checkPhoneNo(phoneNo);
        } catch (Exception exception) {
            logger.error("App - checkPhoneNo Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkKakaoUserName(String kakaoUserName) {
        int idx = userDao.checkKakaoUserName(kakaoUserName);
        return idx;
    }

}