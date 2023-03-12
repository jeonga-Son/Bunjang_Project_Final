package com.example.demo.src.login.kakao.model;

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
public class KakaoUser {
    private int kakaoUserIdx;

    private String kakaoUserName;

    private String kakaoUserBirthday;

    private Timestamp createAt;

    private Timestamp modifyAt;

}
