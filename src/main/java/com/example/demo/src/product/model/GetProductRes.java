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
public class GetProductRes {
    private GetProductInfoRes getProductInfoRes;
    private GetShopRes getShopRes;

}
