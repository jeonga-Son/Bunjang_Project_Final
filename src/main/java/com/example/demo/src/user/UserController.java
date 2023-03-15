package com.example.demo.src.user;

import com.example.demo.src.product.model.PatchProductRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 마이페이지 조회 API
     * [GET] /users/mypage/:userIdx
     * @return BaseResponse<GetMyPageRes>
     */
    @ResponseBody
    @GetMapping("/mypage/{userIdx}") // (GET) 127.0.0.1:9000/users/mypage/:userIdx
    public BaseResponse<GetMyPageRes> getMyPage(@PathVariable("userIdx") int userIdx) {
        try {
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출

            if (userIdx != userIdxByJwt) { // 유저의 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetMyPageRes getMyPageRes = userProvider.getMyPage(userIdx);
            return new BaseResponse<>(getMyPageRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상점 조회 API
     * [GET] /users/store/:userIdx
     * @return BaseResponse<GetStoreRes>
     */
    @ResponseBody
    @GetMapping("/store/{userIdx}") // (GET) 127.0.0.1:9000/users/store/:userIdx
    public BaseResponse<GetStoreRes> getShop(@PathVariable("userIdx") int userIdx) {
        try {
            GetStoreRes getStoreRes = userProvider.getStore(userIdx);
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상점 상품 조회 API
     * [GET] /users/store/:userIdx/products
     * @return BaseResponse<List<GetStoreProductsRes>>
     */
    @ResponseBody
    @GetMapping("/store/{userIdx}/products") // (GET) 127.0.0.1:9000/users/store/:userIdx/products
    public BaseResponse<List<GetStoreProductsRes>> getStoreProducts(@PathVariable("userIdx") int userIdx) {
        try {
            List<GetStoreProductsRes> getStoreProducts = userProvider.getStoreProducts(userIdx);
            return new BaseResponse<>(getStoreProducts);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if (postUserReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if (postUserReq.getPhoneNo() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENO);
        }

        if (postUserReq.getBirthday() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTHDAY);
        }

        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        if(postLoginReq.getName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if(postLoginReq.getPhoneNo() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENO);
        }

        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상점 소개 편집 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<PatchShopInfoReq>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<PatchShopInfoReq> modifyShopInfo(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                System.out.println("userIdx : " + userIdx + " // userIdxByJwt : " + userIdxByJwt);
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PatchShopInfoReq patchShopInfoReq = new PatchShopInfoReq(userIdx, user.getProfileImgUrl(), user.getShopDescription(), user.getName());
            userService.modifyShop(patchShopInfoReq);

            return new BaseResponse<>(patchShopInfoReq);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원 탈퇴 API
     * [PATCH] /users/:userIdx/status
     * @return BaseResponse<PatchDeleteUserRes>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/status")
    public BaseResponse<PatchDeleteUserRes> deleteUser(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetUserRes getUser = userProvider.getUser(userIdx);
            //접근한 유저가 같고, 유저의 상태가 'Deleted'가 아닐 경우 회원 탈퇴 상태로 변경
            String status = getUser.getStatus();
            if (!status.equals("DELETED")) {
            PatchDeleteUserReq patchDeleteUserReq = new PatchDeleteUserReq(userIdx, user.getDeleteReasonContent(), user.getUpdateAt());
            userService.deleteUser(patchDeleteUserReq);

            PatchDeleteUserRes patchDeleteUserRes = new PatchDeleteUserRes();

            patchDeleteUserRes.setUserIdx(patchDeleteUserReq.getUserIdx());
            patchDeleteUserRes.setDeleteReasonContent(patchDeleteUserReq.getDeleteReasonContent());

            return new BaseResponse<>(patchDeleteUserRes);
            } else {
                return new BaseResponse<>(INVALID_USER);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
