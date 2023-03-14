package com.example.demo.src.product.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetShopInfo {
    // 상점 이름, 상점 평점, 상점 팔로워 수, 이 상점의 상품 개수, 이 상점의 후기 개수
    private int userIdx;
    private String name;
    private float avgStar;
    private int followerCount;
    private int productCount;
    private int reviewCount;
    private int isFollowing;
}
