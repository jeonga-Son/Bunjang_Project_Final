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
public class PatchProductReq {
   private int userIdx;
   private List<PostProductImgs> productImgs;
   private String productName;
   private int categoryIdx;
   private int subCategoryIdx;
   private List<PostTags> tag;
   private int price;
   private String description;
}
