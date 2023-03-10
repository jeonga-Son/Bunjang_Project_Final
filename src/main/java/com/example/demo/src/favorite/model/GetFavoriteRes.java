package com.example.demo.src.favorite.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFavoriteRes {
    // 찜 id, 상품 id, 상품 이미지 1장, 상품 이름,  상품 가격, 상품 판매상태, 판매자 이름, 게시일
    private int favoriteIdx;
    private int productIdx;
    private String productImgUrl;
    private String productName;
    private int price;
    private String saleStatus;
    private String  userName;
    private String date;

}
