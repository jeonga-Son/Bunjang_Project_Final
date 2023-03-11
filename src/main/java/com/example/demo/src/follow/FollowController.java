package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.RichException;
import com.example.demo.src.follow.model.GetFollowersRes;
import com.example.demo.src.follow.model.GetFollowingsRes;
import com.example.demo.src.follow.model.PostFollowReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("")
public class FollowController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final FollowProvider followProvider;
    @Autowired
    private final FollowService followService;
    @Autowired
    private final JwtService jwtService;

    public FollowController(FollowProvider followProvider, FollowService followService, JwtService jwtService) {
        this.followProvider = followProvider;
        this.followService = followService;
        this.jwtService = jwtService;
    }

    /** 팔로워 조회 api
     *
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("users/{userIdx}/followers") // (PATCH) 127.0.0.1:9000/users/:userIdx/followers
    public BaseResponse<List<GetFollowersRes>> getFollowerRes(@PathVariable("userIdx") int userIdx) {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (userIdx != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetFollowersRes> getFollowersRes = followProvider.getFollowers(userIdx);
            return new BaseResponse<>(getFollowersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로잉 조회 api
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("users/{userIdx}/followings") // (GET) 127.0.0.1:9000/users/:userIdx/followings
    public BaseResponse<List<GetFollowingsRes>> getFollowingsRes(@PathVariable("userIdx") int userIdx) {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (userIdx != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetFollowingsRes> getFollowingsRes = followProvider.getFollowings(userIdx);
            return new BaseResponse<>(getFollowingsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 팔로우 하기 api
     *
     * @param followingUserIdx
     * @param postFollowReq
     * @return
     */
    @ResponseBody
    @PostMapping("follows") // (POST) 127.0.0.1:9000/follows?followingUserIdx=1
    public BaseResponse<Integer> postFollowRes(@RequestParam("followingUserIdx") int followingUserIdx, @RequestBody PostFollowReq postFollowReq) {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (postFollowReq.getFollowerIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int followIdx = followService.followUser(postFollowReq.getFollowerIdx(), followingUserIdx);
            return new BaseResponse<>(followIdx);
        } catch (RichException richException) {
            return new BaseResponse<>((richException.getStatus()));
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 팔로우 취소 (언팔로우) api
     *
     * @param followingUserIdx
     * @param postFollowReq
     * @return
     */
    @ResponseBody
    @PatchMapping("follows/status") // (PATCH) 127.0.0.1:9000/follows/status?followingUserIdx=1
    public BaseResponse<Integer> patchFollow(@RequestParam("followingUserIdx") int followingUserIdx, @RequestBody PostFollowReq postFollowReq) {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (postFollowReq.getFollowerIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int followIdx = followService.unfollow(postFollowReq.getFollowerIdx(), followingUserIdx);
            return new BaseResponse<>(followIdx);
        } catch (RichException richException) {
            return new BaseResponse<>((richException.getStatus()));
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
