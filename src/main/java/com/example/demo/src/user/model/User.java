package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 유저 id, 이름, 휴대폰번호, 생년월일, 주소, 위도, 경도, 생성일, 수정일, 상태, 프로필이미지URL, 상품 소개
    private int userIdx;

    private String name;

    private String phoneNo;

    private Date birthday;

    private String address;

    private Float latitude;

    private Float longitude;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createAt;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp updateAt;

    private String status;

    private String profileImgUrl;

    private String shopDescription;

//    private String password;

    private String deleteReasonContent;
}