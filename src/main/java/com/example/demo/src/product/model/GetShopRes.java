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
public class GetShopRes {
    // 상점 이름, 상점 평점, 상점 팔로워 수, 이 상점의 상품 개수, 이 상점의 후기 개수
    // 이 상점의 상품 list (대표사진, 금액, 상품 이름)
    // 후기 List (2개까지 출력, 리뷰id, 후기 별점, 후기 내용, 후기 사진, 후기 작성자, 작성일자)
    private List<GetReviewList> reviews;
    private List<GetProductList> products;
    private GetShopInfo getShopInfo;




}
