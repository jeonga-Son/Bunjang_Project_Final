package com.example.demo.src.login.kakao;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.login.kakao.model.KakaoUser;
import com.example.demo.src.login.kakao.model.PostKakaoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/oauth")
public class KakaoController {

    @Autowired
    private final KakaoService kakaoService;
    @Autowired
    private final KakaoProvider kakaoProvider;

    public KakaoController(KakaoService kakaoLoginservice, KakaoProvider kakaoProvider) {
        this.kakaoService = kakaoLoginservice;
        this.kakaoProvider = kakaoProvider;
    }

    /**
     * 카카오 로그인 API
     * [GET] /oauth/kakao
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/kakao")
    public BaseResponse<String> kakaoCallback(String code){
        String result = kakaoService.getToken(code);

        return new BaseResponse<String>(result);

    }

}