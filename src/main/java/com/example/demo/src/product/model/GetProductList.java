package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductList {
    // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름, 찜 여부)
    private int productIdx;
    private String productImgUrl;
    private int price;
    private String productName;
    private int isFavorite;

}
