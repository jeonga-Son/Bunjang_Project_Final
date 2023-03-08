package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.follow.model.GetFollowersRes;
import com.example.demo.src.follow.model.GetFollowingsRes;
import com.example.demo.src.follow.model.PostFollowReq;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
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


    /**
     * 팔로잉 조회 api
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{userIdx}/followings") // (GET) 127.0.0.1:9000/users/:userIdx/followings
    public BaseResponse<List<GetFollowingsRes>> getFollowingsRes(@PathVariable("userIdx") int userIdx) {
        try{
            List<GetFollowingsRes> getFollowingsRes = followProvider.getFollowings(userIdx);
            return new BaseResponse<>(getFollowingsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 팔로우 하기 api
     *
     * @param followerIdx
     * @param postFollowReq
     * @return
     */
    @ResponseBody
    @PostMapping("/{userIdx}/follow") // (POST) 127.0.0.1:9000/users/:userIdx/follow
    public BaseResponse<Integer> postFollowRes(@PathVariable("userIdx") int followerIdx, @RequestBody PostFollowReq postFollowReq) {
        try{
            int followIdx = followService.followUser(followerIdx, postFollowReq.getUserIdx());
            return new BaseResponse<>(followIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 팔로우 취소 (언팔로우) api
     *
     * @param followerIdx
     * @param postFollowReq
     * @return
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/followStatus") // (PATCH) 127.0.0.1:9000/users/:userIdx/followStatus
    public BaseResponse<Integer> patchFollow(@PathVariable("userIdx") int followerIdx, @RequestBody PostFollowReq postFollowReq) {
        try{
            int followIdx = followService.unfollow(followerIdx, postFollowReq.getUserIdx());
            return new BaseResponse<>(followIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 팔로워 조회 api
     *
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{userIdx}/followers") // (PATCH) 127.0.0.1:9000/users/:userIdx/followers
    public BaseResponse<List<GetFollowersRes>> getFollowerRes(@PathVariable("userIdx") int userIdx) {
        try{
            List<GetFollowersRes> getFollowersRes = followProvider.getFollowers(userIdx);
            return new BaseResponse<>(getFollowersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
