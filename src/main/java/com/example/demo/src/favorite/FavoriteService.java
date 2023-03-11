package com.example.demo.src.favorite;


import com.example.demo.config.BaseException;
import com.example.demo.config.RichException;
import com.example.demo.src.favorite.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class FavoriteService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FavoriteDao favoriteDao;
    private final FavoriteProvider favoriteProvider;
    private final JwtService jwtService;


    @Autowired
    public FavoriteService(FavoriteDao favoriteDao, FavoriteProvider favoriteProvider, JwtService jwtService) {
        this.favoriteDao = favoriteDao;
        this.favoriteProvider = favoriteProvider;
        this.jwtService = jwtService;

    }

    // 찜 하기 메서드
    public int createFavorite(int productIdx, PostFavoriteReq postFavoriteReq) throws BaseException, RichException {
        try {
            int userIdx = postFavoriteReq.getUserIdx();

            // validation : 이미 찜하고 있는지?
            if (getFavoriteIdx(userIdx, productIdx, "ACTIVE") != 0)
                throw new RichException(DUPLICATED_FAVORITE);

            // 이전에 찜했다가 취소한 상품이면, 새로 추가하지 않고 favoriteStatus 변경
            int favoriteIdx = getFavoriteIdx(userIdx, productIdx, "INACTIVE");
            if (favoriteIdx != 0)
                favoriteDao.reFavorite(favoriteIdx);
            else
                favoriteIdx = favoriteDao.insertFavorite(userIdx, productIdx);


            return favoriteIdx;
        } catch (RichException richException) {
            logger.error("App - createFavorite Service Error", richException);
            throw new RichException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - createFavorite Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 찜 취소 메서드
    public String cancelFavorite(int userIdx, int productIdx) throws BaseException, RichException {
        try {
            // validation : 존재하는 찜인가?
            int favoriteIdx = getFavoriteIdx(userIdx, productIdx, "ACTIVE");
            if(favoriteIdx == 0) // 존재 안하면 ( 찜을 한 적이 없거나 / 취소했거나 )
                throw new RichException(EMPTY_FAVORITE);

//            // 회원용 API : 권한이 있는 유저인가? (이 찜을 한 유저인가?)
//            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
//            if (getUserIdxOfFavorite(favoriteIdx) != userIdxByJwt)
//                throw new RichException(INVALID_USER_JWT);


            String resultMessage = "찜 해제 완료";
            int result = favoriteDao.updateFavoriteStatus(favoriteIdx);

            if (result == 1)
                return resultMessage;
            else
                throw new BaseException(DATABASE_ERROR);

        } catch (RichException richException) {
            logger.error("App - cancelFavorite Service Error", richException);
            throw new RichException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - cancelFavorite Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getFavoriteIdx (int userIdx, int productIdx, String status) throws BaseException { // 존재하면 favoriteIdx 출력, 없으면 0 출력
        try {
            int result = favoriteDao.getFavoriteIdx(userIdx, productIdx, status);

            return result;
        } catch (Exception exception) {
            logger.error("App - checkFavoriteExists Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getFavoriteIdx (int userIdx, int productIdx) throws BaseException { // 존재하면 favoriteIdx 출력, 없으면 0 출력
        try {

            int result = favoriteDao.getFavoriteIdx(userIdx, productIdx);

            return result;
        } catch (Exception exception) {
            logger.error("App - checkFavoriteExists Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int getUserIdxOfFavorite(int favoriteIdx) throws BaseException {
        try {
            int result = favoriteDao.getUserIdxOfFavorite(favoriteIdx);

            return result;
        } catch (Exception exception) {
            logger.error("App - getUserIdxOfFavorite Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
