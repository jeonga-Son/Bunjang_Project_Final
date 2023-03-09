package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.favorite.model.PostFavoriteReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final FavoriteProvider favoriteProvider;
    @Autowired
    private final FavoriteService favoriteService;
    @Autowired
    private final JwtService jwtService;

    public FavoriteController(FavoriteProvider favoriteProvider, FavoriteService favoriteService, JwtService jwtService) {
        this.favoriteProvider = favoriteProvider;
        this.favoriteService = favoriteService;
        this.jwtService = jwtService;
    }


    // 찜 하기 api
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/favorites?productIdx=1
    public BaseResponse<Integer> postFavorite(@RequestBody PostFavoriteReq postFavoriteReq, @RequestParam("productIdx") int productIdx) {
        try{
            int favoriteIdx = favoriteService.createFavorite(productIdx, postFavoriteReq);
            return new BaseResponse<>(favoriteIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
