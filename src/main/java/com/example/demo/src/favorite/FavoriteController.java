package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.favorite.model.GetFavoriteRes;
import com.example.demo.src.favorite.model.PostFavoriteReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse<Integer> postFavorite( @RequestParam("productIdx") int productIdx, @RequestBody PostFavoriteReq postFavoriteReq) {
        try{
            int favoriteIdx = favoriteService.createFavorite(productIdx, postFavoriteReq);
            return new BaseResponse<>(favoriteIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 찜 목록 조회 api
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/favorites?userIdx=1
    public BaseResponse<List<GetFavoriteRes>>getFavoriteRes(@RequestParam("userIdx") int userIdx) {
        try{
            List<GetFavoriteRes> getFavoriteRes = favoriteProvider.getFavorites(userIdx);
            return new BaseResponse<>(getFavoriteRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/{favoriteIdx}/status") // (PATCH) 127.0.0.1:9000/favorites/:favoriteIdx/status
    public BaseResponse<String>patchFavoriteStatus(@PathVariable("favoriteIdx") int favoriteIdx) {
        try{
            String message = favoriteService.cancelFavorite(favoriteIdx);

            return new BaseResponse<>(message);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
