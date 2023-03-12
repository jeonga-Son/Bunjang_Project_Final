package com.example.demo.src.login.kakao.model;

import com.example.demo.src.product.model.GetProductList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetKakaoMyPageRes {

    private int kakaoUserIdx;

    private String kakaoUserName;

    private String profileImgUrl;

    private float avgStar;

    private int point;

    private int followerCount;

    private int followingCount;

    private List<GetProductList> productList;
}
