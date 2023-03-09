package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.src.favorite.model.GetFavoriteRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FavoriteProvider {

    private final FavoriteDao favoriteDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public FavoriteProvider(FavoriteDao favoriteDao, JwtService jwtService) {
        this.favoriteDao = favoriteDao;
        this.jwtService = jwtService;
    }

    public List<GetFavoriteRes> getFavorites(int userIdx) throws BaseException{
        try{
            List<GetFavoriteRes> getFavoriteRes = favoriteDao.getFavorites(userIdx);
            return getFavoriteRes;

        } catch(Exception exception) {
            logger.error("App - getFavoriteRes Provider Error");
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
