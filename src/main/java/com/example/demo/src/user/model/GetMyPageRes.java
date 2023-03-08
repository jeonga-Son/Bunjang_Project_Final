package com.example.demo.src.user.model;


import com.example.demo.src.product.model.PostProductImgs;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyPageRes {
    // 유저 id, 이름, 프로필이미지URL, 포인트 잔액, 평점평균, 팔로우 id, 팔로잉 id
    private int userIdx;

    private String name;

    private String profileImgUrl;

    private String shopDescription;

    private int point;

    private int followerUserIdx;

    private int followingUserIdx;

    private List<PostProductImgs> productImgs;

}
