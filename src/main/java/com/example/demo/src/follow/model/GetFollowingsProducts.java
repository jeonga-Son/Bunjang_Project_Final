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
public class GetFollowingsProducts {
    // 상품 id, 상품 이미지 url, 가격
    private int productIdx;
    private String productImgUrl;
    private int price;
}
