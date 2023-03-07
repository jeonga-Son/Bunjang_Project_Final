package com.example.demo.src.product.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostProductImgs {
    // 상품 id, 금액, 상품 이름, 게시일, 판매상태, 서브 카테고리 id, 서브 카테고리 이름, 작성자 id, 채팅 수, 찜 수
    // 상품 이미지 불러오기 List
    // 키워드 불러오기 List
    private String productImgUrl;

}
