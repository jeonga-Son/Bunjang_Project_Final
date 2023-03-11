package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.RichException;
import com.example.demo.src.favorite.model.*;
import com.example.demo.src.product.model.ProductUserIdx;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

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


    /**
     * 찜 하기 api
     *
     * @param productIdx
     * @param postFavoriteReq
     * @return
     */
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/favorites?productIdx=1
    public BaseResponse<Integer> postFavorite(@RequestParam("productIdx") int productIdx, @RequestBody PostFavoriteReq postFavoriteReq) {
        try {
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (postFavoriteReq.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int favoriteIdx = favoriteService.createFavorite(productIdx, postFavoriteReq);
            return new BaseResponse<>(favoriteIdx);
        } catch (RichException richException) {
            return new BaseResponse<>((richException.getStatus()));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 찜 목록 조회 api
     *
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/favorites?userIdx=1
    public BaseResponse<List<GetFavoriteRes>> getFavoriteRes(@RequestParam("userIdx") int userIdx) {
        try {
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (userIdx != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetFavoriteRes> getFavoriteRes = favoriteProvider.getFavorites(userIdx);
            return new BaseResponse<>(getFavoriteRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 찜 해제 api
     *
     * @param productIdx
     * @return
     */
    @ResponseBody
    @PatchMapping("/status") // (PATCH) 127.0.0.1:9000/favorites/status?productIdx
    public BaseResponse<String> patchFavoriteStatus(@RequestParam("productIdx") int productIdx, @RequestBody ProductUserIdx productUserIdx) {
        try {
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (productUserIdx.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            String message = favoriteService.cancelFavorite(productUserIdx.getUserIdx(), productIdx);

            return new BaseResponse<>(message);
        } catch (RichException richException) {
            return new BaseResponse<>((richException.getStatus()));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}
