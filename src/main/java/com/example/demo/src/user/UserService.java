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

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final JwtService jwtService;
    private final UserProvider userProvider;

    @Autowired
    public UserService(UserDao userDao, JwtService jwtService, UserProvider userProvider) {
        this.userDao = userDao;
        this.jwtService = jwtService;
        this.userProvider = userProvider;
    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복
        if(userProvider.checkPhoneNo(postUserReq.getPhoneNo()) ==1){
            throw new BaseException(POST_USERS_EXISTS_PHONENO);
        }

        String pwd;
        try{
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);

        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);

            return new PostUserRes(userIdx,jwt);
        } catch (Exception exception) {
            logger.error("App - createUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteUser(PatchDeleteUserReq patchDeleteUserReq) throws BaseException {
        try {
            int result = userDao.deleteUser(patchDeleteUserReq);

            if(result == 0) {
                throw new BaseException(DELETE_FAIL_USER);
            }
        } catch (Exception exception) {
            logger.error("App - deleteUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyShop(PatchShopInfoReq patchShopInfoReq) throws BaseException {
        try {
            int result = userDao.modifyShop(patchShopInfoReq);
            if(result == 0) {
                throw new BaseException(MODIFY_FAIL_SHOP);
            }
        } catch (Exception exception) {
            logger.error("App - modifyShop Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
