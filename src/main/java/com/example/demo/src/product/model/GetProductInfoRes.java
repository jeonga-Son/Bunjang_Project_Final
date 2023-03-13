package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductInfoRes {
    // 상품 id, 찜 여부, 금액, 상품 이름, 게시일, 판매상태, 서브 카테고리 id, 서브 카테고리 이름, 작성자 id, 채팅 수, 찜 수
    // 상품 이미지 불러오기 List
    // 키워드 불러오기 List
    private int productIdx;
    private int isFavorite;
    private int price;
    private String productName;
    private int count;
    private String productStatus;
    private String isExchange;
    private String description;
    private String date;
    private String saleStatus;
    private int subCategoryIdx;
    private String subCategoryName;
    private int userIdx;
    private int chatCount;
    private int favoriteCount;
    private List<PostProductImgs> productImgs;
    private List<PostTags> tags;


}
