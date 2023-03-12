package com.example.demo.src.login.kakao;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/oauth")
public class KakaoController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final UserProvider userProvider;

    public KakaoController(UserService userService, UserProvider userProvider) {
        this.userService = userService;
        this.userProvider = userProvider;
    }

    /**
     * 카카오 로그인 API
     * [GET] /oauth/kakao
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/kakao")
    public BaseResponse<String> kakaoCallback(String code) throws ParseException {
        String result = userService.getToken(code);

        return new BaseResponse<String>(result);

    }

}