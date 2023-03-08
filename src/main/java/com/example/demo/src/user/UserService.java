package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if (userDao.checkUser(postUserReq) == 1) {
            throw new BaseException(POST_USERS_EXISTS_PHONENO);
        }

        try{
            int userIdx = userDao.createUser(postUserReq);
            String name = postUserReq.getName();
            String phoneNo = postUserReq.getPhoneNo();
            Date birthday = postUserReq.getBirthday();
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(userIdx,name, phoneNo, birthday, jwt);
        } catch (Exception exception) {
            logger.error("App - createUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);

        }
    }

    public void deleteUser(int userIdx) throws BaseException {
        try {
            int result = userDao.deleteUser(userIdx);
            if(result == 0) {
                throw new BaseException(DELETE_FAIL_USER);
            }
        } catch (Exception exception) {
            logger.error("App - deleteUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyShop(int userIdx, PatchShopInfoReq patchShopInfoReq) throws BaseException {
        try {
            int result = userDao.modifyShop(userIdx, patchShopInfoReq);
            if(result == 0) {
                throw new BaseException(MODIFY_FAIL_SHOP);
            }
        } catch (Exception exception) {
            logger.error("App - modifyShop Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
