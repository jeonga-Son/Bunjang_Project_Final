package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReviews {
    // 작성자 id, 별점, 작성자 이름, 리뷰 내용, 리뷰 이미지, 리뷰 작성일, 거래 상품 id, 상품 이름
    private int userIdx;
    private int star;
    private  String userName;
    private String content;
    private String reviewImgUrl;
    private String date;
    private int productIdx;
    private String productName;

}
