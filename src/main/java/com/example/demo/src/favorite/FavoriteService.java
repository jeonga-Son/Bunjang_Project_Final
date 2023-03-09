package com.example.demo.src.favorite;


import com.example.demo.config.BaseException;
import com.example.demo.src.favorite.model.PostFavoriteReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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

    // 리뷰 쓰기 메서드
    public int createFavorite(int productIdx, PostFavoriteReq postFavoriteReq) throws BaseException {
        try {
            int userIdx = postFavoriteReq.getUserIdx();
            int favoriteIdx = favoriteDao.insertFavorite(productIdx, userIdx);

            return favoriteIdx;

        } catch (Exception exception) {
            logger.error("App - createFavorite Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
