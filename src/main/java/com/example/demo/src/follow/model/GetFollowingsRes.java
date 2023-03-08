package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFollowingsRes {
    // 유저 id, 유저 프로필 이미지 url, 유저 이름, 상품 개수, 팔로워 수, 상품 List
    private int userIdx;
    private String userProfileImgUrl;
    private String userName;
    private int productCount;
    private int followerCount;
    private List<GetFollowingsProducts> products;
}
