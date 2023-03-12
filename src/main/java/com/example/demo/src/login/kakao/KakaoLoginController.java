package com.example.demo.src.login.kakao;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.login.kakao.model.KakaoProfile;
import com.example.demo.src.login.kakao.model.KakaoUser;
import com.example.demo.src.login.kakao.model.PostKakaoUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("")
public class KakaoLoginController {

    @Autowired
    private final KakaoLoginService kakaoLoginservice;

    public KakaoLoginController(KakaoLoginService kakaoLoginservice) {
        this.kakaoLoginservice = kakaoLoginservice;
    }

    @ResponseBody
    @GetMapping("/oauth/kakao")
    public BaseResponse<String> kakaoCallback(String code){
        //POST 방식으로 key=value 데이터를 요청(카카오 쪽으로)
        RestTemplate rt = new RestTemplate(); //http 요청을 간단하게 해줄 수 있는 클래스

        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "554ec8212c43b13071907450aa3d6f11");
        params.add("redirect_uri", "http://localhost:9000/oauth/kakao");
        params.add("code", code);

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        //실제로 요청하기
        //Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답을 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //Gson Library, JSON SIMPLE LIBRARY, OBJECT MAPPER(Check)
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        //Model과 다르게 되있으면 그리고 getter setter가 없으면 오류가 날 것이다.
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
            System.out.println("카카오 엑세스 토큰:" + oauthToken.getAccess_token());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 2.

        RestTemplate rt2 = new RestTemplate(); //http 요청을 간단하게 해줄 수 있는 클래스

        //HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        //실제로 요청하기
        //Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답을 받음.
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        //Model과 다르게 되있으면 그리고 getter setter가 없으면 오류가 날 것이다.
        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //
        System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
        System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());
        System.out.println("카카오 닉네임 : " + kakaoProfile.getProperties().getNickname());
        System.out.println("카카오 생일 : " +  kakaoProfile.getKakao_account().getBirthday());

        System.out.println("서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId());
        System.out.println("서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());

        UUID garbagePassword = UUID.randomUUID();
        System.out.println("서버 패스워드 " + garbagePassword);

        PostKakaoUser postKakaoUser = new PostKakaoUser();
        postKakaoUser.setKakaoUserName(kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId());
        postKakaoUser.setKakaoUserBirthday(kakaoProfile.getKakao_account().getBirthday());
        postKakaoUser.setCreateAt(Timestamp.valueOf(LocalDateTime.now()));

        KakaoUser kakaoUser = new KakaoUser();
        int findIdx = kakaoLoginservice.checkUser(postKakaoUser.getKakaoUserName());

        String result ="";
        if(findIdx == 1) {
            result = "기존 회원입니다.";
            return new BaseResponse<String>(result);
        } else {
            kakaoLoginservice.createUser(postKakaoUser);
            result = "카카오 로그인 요청에 성공하였습니다.";
            return new BaseResponse<String>(result);
        }
    }
}