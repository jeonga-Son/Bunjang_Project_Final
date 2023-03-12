package com.example.demo.src.login.kakao;

import com.example.demo.src.login.kakao.model.KakaoUser;
import com.example.demo.src.login.kakao.model.PostKakaoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KakaoLoginService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final KakaoLoginDao kakaoLoginDao;

    @Autowired
    public KakaoLoginService(KakaoLoginDao kakaoLoginDao) {
        this.kakaoLoginDao = kakaoLoginDao;
    }

    public void createUser(PostKakaoUser postKakaoUser) {
        kakaoLoginDao.createUser(postKakaoUser);
    }

    public int checkUser(String kakaoUserName) {
        int idx = kakaoLoginDao.checkUser(kakaoUserName);
        return idx;
    }
}
