package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewList {
    // 후기 List (2개까지 출력, 리뷰id, 후기 별점, 후기 내용, 후기 사진, 후기 작성자 이름, 작성일자)
    private int reviewIdx;
    private float star;
    private String content;
    private String reviewImgUrl;
    private int reviewImgCount;
    private String userName;
    private String date;
}
