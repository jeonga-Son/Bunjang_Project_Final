package com.example.demo.src.point;

import com.example.demo.config.BaseException;
import com.example.demo.src.point.model.GetPointListRes;
import com.example.demo.src.point.model.GetPointRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@Service
public class PointProvider {
    private final PointDao pointDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final UserDao userDao;
    @Autowired
    private final JwtService jwtService;

    @Autowired
    public PointProvider(PointDao pointDao, UserDao userDao, JwtService jwtService) {
        this.pointDao = pointDao;
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<GetPointRes> getMyPoint(int userIdx) throws BaseException {
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

        try{
            List<GetPointRes> getPointRes = pointDao.getMyPoint(userIdx);
            return getPointRes;
        } catch (Exception exception) {
            logger.error("App - getMyPoint Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPointListRes> getMyPointList(int userIdx) throws BaseException {
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

        try{
            List<GetPointListRes> getPointListRes = pointDao.getMyPointList(userIdx);
            return getPointListRes;
        } catch (Exception exception) {
            logger.error("App - getMyPointList Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
