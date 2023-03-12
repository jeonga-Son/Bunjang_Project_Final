//package com.example.demo.src.login.kakao2;
//
//import com.example.demo.config.BaseResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/oauth")
//public class KakaoController {
//
//    @Autowired
//    private final KakaoService kakaoService;
//
//    public KakaoController(KakaoService kakaoService) {
//        this.kakaoService = kakaoService;
//    }
//
//    /**
//     * 카카오 callback
//     * [GET] /oauth/kakao
//     */
//    @ResponseBody
//    @GetMapping("/kakao")
//    public BaseResponse<String> kakaoCallback(@RequestParam String code){
//        String accessToken = kakaoService.getKakaoAccessToken(code);
//        String response = "카카오 로그인 요청에 성공하였습니다.";
//        return  new BaseResponse<String>(response);
//    }
//}
