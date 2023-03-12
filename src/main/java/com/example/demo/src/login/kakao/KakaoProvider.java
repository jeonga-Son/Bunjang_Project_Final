package com.example.demo.src.login.kakao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KakaoProvider {

    private final KakaoDao kakaoDao;

    @Autowired
    public KakaoProvider(KakaoDao kakaoDao) {
        this.kakaoDao = kakaoDao;
    }

    public int checkUser(String kakaoUserName) {
        int idx = kakaoDao.checkUser(kakaoUserName);
        return idx;
    }
}
