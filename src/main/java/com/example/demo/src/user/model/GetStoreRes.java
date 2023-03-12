package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStoreRes {
    // 유저 id, 이름, 프로필이미지URL, 포인트 잔액, 평점평균, 팔로우 id, 팔로잉 id
    private int userIdx;

    private String name;

    private String profileImgUrl;

    private String shopDescription;

    private int followerCount;

    private int followingCount;

}
