package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
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

        // 존재하는 유저(=상점)인지 체크
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_EXISTS);
        }

        // 조회하는 유저(=상점)가 삭제되거나 비활성화 된 유저(=상점)인지 체크
        if (userDao.checkUserStatus(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }

        // 회원용 API
        // jwt에서 userIdx 추출
        int userIdxByJwt = jwtService.getUserIdx();

        // 유저(=상점)의 userIdx != jwt에서 추출한 userIdx
        if (userIdx != userIdxByJwt) {
            throw new BaseException(INVALID_USER_JWT);
        }

        try {
            GetMyPageRes getMyPageRes = userDao.getMyPage(userIdx);
            return getMyPageRes;

        } catch (Exception exception) {
            logger.error("App - getUser Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreRes getStore(int userIdx) throws BaseException {

        // 존재하는 유저(=상점)인지 체크
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_EXISTS);
        }

        // 조회하는 유저(=상점)가 삭제되거나 비활성화 된 유저(=상점)인지 체크
        if (userDao.checkUserStatus(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }

        try {
            GetStoreRes getStoreRes = userDao.getStore(userIdx);
            return getStoreRes;
        } catch (Exception exception) {
            logger.error("App - getShop Provider Error", exception);
        }
        return null;
    }

    public List<GetStoreProductsRes> getStoreProducts(int userIdx) throws BaseException {
        // 존재하는 유저(=상점)인지 체크
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_EXISTS);
        }

        // 조회하는 유저(=상점)가 삭제되거나 비활성화 된 유저(=상점)인지 체크
        if (userDao.checkUserStatus(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }

        // 해당 상점에 상품이 존재하는지 체크
        if (userDao.checkUserProductStatus(userIdx) == 0) {
            throw new BaseException(USERS_PRODUCTS_NOT_EXISTS);
        }

        try {
            List<GetStoreProductsRes> getStoreProductsRes = userDao.getStoreProducts(userIdx);
            return getStoreProductsRes;
        } catch (Exception exception) {
            logger.error("App - getShop Provider Error", exception);
        }
        return null;
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
            User user = userDao.checkUser(postLoginReq);

            // 존재하는 유저(=상점)인지 체크
            if (userDao.checkUserIdx(user.getUserIdx()) == 0) {
                throw new BaseException(USERS_NOT_EXISTS);
            }

            // 조회하는 유저(=상점)가 삭제되거나 비활성화 된 유저(=상점)인지 체크
            if (userDao.checkUserStatus(user.getUserIdx()) == 0) {
                throw new BaseException(USERS_NOT_FOUND);
            }

        try {
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

    // 유저 id가 존재하는 지 체크
    public int checkUserIdx(int userIdx) {
        int findUserIdx = userDao.checkUserIdx(userIdx);
        return findUserIdx;
    }

}