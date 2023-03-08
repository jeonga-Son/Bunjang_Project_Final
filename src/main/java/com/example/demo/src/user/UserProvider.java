package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
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

    public GetShopRes getShop(int userIdx) throws BaseException {

        if (userDao.getUser(userIdx).equals(null)) {
            throw new BaseException(POST_USERS_EXISTS_PHONENO);
        }

        try {
            GetShopRes getShopRes = userDao.getShop(userIdx);
            return getShopRes;
        } catch (Exception exception) {
            logger.error("App - getShop Provider Error", exception);
        }
        return null;
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        try {
            User user = userDao.getPwd(postLoginReq);

            String encryptPwd;
            try {
                encryptPwd = new SHA256().encrypt(postLoginReq.getPassword());
            } catch (Exception exception) {
                logger.error("App - logIn Provider Encrypt Error", exception);
                throw new BaseException(PASSWORD_DECRYPTION_ERROR);
            }

            if(user.getPassword().equals(encryptPwd)){
                int userIdx = user.getUserIdx();
                String jwt = jwtService.createJwt(userIdx);
                return new PostLoginRes(userIdx,jwt);
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
}
