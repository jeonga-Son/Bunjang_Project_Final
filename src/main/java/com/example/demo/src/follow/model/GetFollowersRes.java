package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFollowersRes {
    // 팔로우 id, 유저 id, 유저 이름, 유저 프로필 이미지, 팔로워 수, 상품 수
    private int followIdx;
    private int userIdx;
    private String name;
    private String profileImgUrl;
    private int followerCount;
    private int productCount;

}
