package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final JwtService jwtService;

    public ReviewController(ReviewProvider reviewProvider, ReviewService reviewService, JwtService jwtService) {
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
        this.jwtService = jwtService;
    }


    // 리뷰 쓰기 api
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/reviews?productIdx=1
    public BaseResponse<Integer> postReviews(@RequestBody PostReviewReq postReviewReq, @RequestParam("productIdx") int productIdx) {
        try{
            int reviewIdx = reviewService.createReview(productIdx, postReviewReq);
            return new BaseResponse<>(reviewIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 리뷰 삭제 api
    @ResponseBody
    @PatchMapping("/{reviewIdx}/status") // (POST) 127.0.0.1:9000/reviews/:reviewIdx/status
    public BaseResponse<Integer> patchReviewStatus(@PathVariable("reviewIdx") int reviewIdx) {
        try{
            int deletedReviewIdx = reviewService.deleteReview(reviewIdx);
            return new BaseResponse<>(deletedReviewIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 상점 리뷰 조회 api
    @ResponseBody
    @GetMapping("") // (POST) 127.0.0.1:9000/reviews?userIdx=1
    public BaseResponse<GetReviewsRes> getReviews(@RequestParam("userIdx") int userIdx) {
        try{
            GetReviewsRes getReviewsRes = reviewProvider.getReviewsRes(userIdx);
            return new BaseResponse<>(getReviewsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
