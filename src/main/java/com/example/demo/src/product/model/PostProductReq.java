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
public class PostProductReq {
   private int userIdx;
   private List<PostProductImgs> productImgs;
//   @NotBlank(message = "상품 제목(이름)을 입력해주세요")
   private String productName;
//   @NotNull(message = "카테고리를 입력해주세요")
   private int subCategoryIdx;
   private List<PostTags> tags;
//   @NotNull(message = "상품 가격을 입력해주세요")
   private int price;
//   @NotBlank(message = "상품 설명을 입력해주세요")
   private String description;
   private int count;
   private String productStatus;
   private String isExchange;
}
