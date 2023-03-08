package com.example.demo.src.user.model;


import com.example.demo.src.product.model.PostProductImgs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRes {
    private int userIdx;

    private String name;

    private String phoneNo;

    private Date birthday;

    private String address;

    private Float latitude;

    private Float longitude;

    private String status;

    private String profileImgUrl;

    private String shopDescription;

}
