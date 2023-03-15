package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostProductReq {
   private int userIdx;
   private List<PostProductImgs> productImgs;

   private String productName;
   @NotNull(message = "카테고리를 입력해주세요.")
   private int subCategoryIdx;
   private List<PostTags> tags;
   @NotNull(message = "상품 가격을 입력해주세요.")
   @Max(value = 999999999, message="가격은 999,999,999원 이하여야 합니다.")
   @Min(value = 500, message="가격은 500원 이상이어야 합니다.")
   private int price;
   private String description;
   private int count;
   private String productStatus;
   private String isExchange;
}
