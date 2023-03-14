package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewsRes {
    // 거래 후기 개수, List<작성자 id, 별점, 작성자 이름, 리뷰 내용, 리뷰 이미지, 이미지 개수, 리뷰 작성일, 거래 상품 id, 상품 이름>
    private int reviewCount;
    private List<GetReviews> getReviewsList;

}
