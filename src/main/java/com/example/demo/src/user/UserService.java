package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.login.kakao.model.KakaoProfile;
import com.example.demo.src.login.kakao.model.OAuthToken;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final JwtService jwtService;
    private final UserProvider userProvider;

    @Autowired
    public UserService(UserDao userDao, JwtService jwtService, UserProvider userProvider) {
        this.userDao = userDao;
        this.jwtService = jwtService;
        this.userProvider = userProvider;
    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 휴대폰번호 중복 체크
        if(userProvider.checkPhoneNo(postUserReq.getPhoneNo()) ==1){
            throw new BaseException(POST_USERS_EXISTS_PHONENO);
        }

        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            String name = postUserReq.getName();
            String phoneNo = postUserReq.getPhoneNo();
            Date birthday = postUserReq.getBirthday();
            String resultMessage = "'" + name + "'" + "님 회원가입을 환영합니다.";
            return new PostUserRes(userIdx, name, phoneNo, birthday, jwt, resultMessage);
        } catch (Exception exception) {
            logger.error("App - createUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public PatchDeleteUserRes deleteUser(PatchDeleteUserReq patchDeleteUserReq, int userIdx) throws BaseException {
        // 존재하는 유저(=상점)인지 체크
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_EXISTS);
        }

        // 조회하는 유저(=상점)가 삭제되거나 비활성화 된 유저(=상점)인지 체크
        if (userDao.checkUserStatus(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }

        //jwt에서 idx 추출.
        int userIdxByJwt = jwtService.getUserIdx();
        //userIdx와 접근한 유저가 같은지 확인
        if (userIdx != userIdxByJwt) {
            throw new BaseException(INVALID_USER_JWT);
        }

        GetUserRes getUser = userProvider.getUser(userIdx);
        String status = getUser.getStatus();

        try {
            //접근한 유저가 같고, 유저의 상태가 'Deleted'가 아닐 경우 회원 탈퇴 상태로 변경
            if (!status.equals("DELETED")) {
                PatchDeleteUserRes patchDeleteUserRes = userDao.deleteUser(patchDeleteUserReq, userIdx);

                return patchDeleteUserRes;
            } else {
                throw new BaseException(DELETE_FAIL_USER);
            }
        } catch (Exception exception) {
            logger.error("App - deleteUser Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PatchShopInfoRes modifyShop(int userIdx, PatchShopInfoReq patchShopInfoReq) throws BaseException {
        // 존재하는 유저(=상점)인지 체크
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_EXISTS);
        }

        // 조회하는 유저(=상점)가 삭제되거나 비활성화 된 유저(=상점)인지 체크
        if (userDao.checkUserStatus(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }

        //jwt에서 idx 추출.
        int userIdxByJwt = jwtService.getUserIdx();
        //userIdx와 접근한 유저가 같은지 확인
        if (userIdx != userIdxByJwt) {
            throw new BaseException(INVALID_USER_JWT);
        }

        try {
            int result = userDao.modifyShop(userIdx,patchShopInfoReq);
            if(result == 0) {
                throw new BaseException(MODIFY_FAIL_SHOP);
            }

            PatchShopInfoRes patchShopInfoRes = new PatchShopInfoRes(
                    userIdx,
                    patchShopInfoReq.getProfileImgUrl(),
                    patchShopInfoReq.getShopDescription(),
                    patchShopInfoReq.getName()
            );
            return patchShopInfoRes;

        } catch (Exception exception) {
            logger.error("App - modifyShop Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getToken(String code) throws ParseException {
        //POST 방식으로 key=value 데이터를 요청
        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "554ec8212c43b13071907450aa3d6f11");
        params.add("redirect_uri", "http://dev.rising-bunjang.store:9000/oauth/kakao");
        params.add("code", code);

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);


        //Http 요청하기
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //Gson Library, JSON SIMPLE LIBRARY, OBJECT MAPPER(Check)
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;

        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
            System.out.println("카카오 엑세스 토큰:" + oauthToken.getAccess_token());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RestTemplate rt2 = new RestTemplate(); //http 요청을 간단하게 해줄 수 있는 클래스

        //HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        //Http 요청하기
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
        System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());
        System.out.println("카카오 닉네임 : " + kakaoProfile.getProperties().getNickname());
        System.out.println("카카오 생일 : " +  kakaoProfile.getKakao_account().getBirthday());

        System.out.println("서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId());
        System.out.println("서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());

        UUID garbagePassword = UUID.randomUUID();
        System.out.println("서버 패스워드 " + garbagePassword);

        String empty = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = "0000-00-00";

        Date emptyDate = new Date(sdf.parse(date).getTime());


        PostUserReq postUserReq = new PostUserReq();
        postUserReq.setName(String.valueOf(kakaoProfile.getId()));
        postUserReq.setPhoneNo(empty);
        postUserReq.setBirthday(emptyDate);

        User user = new User();
        int findIdx = userProvider.checkKakaoUserName(postUserReq.getName());

        String result ="";
        if(findIdx == 1) {
            result = "기존 회원입니다.";
        } else {
            userDao.createUser(postUserReq);
            result = "카카오 로그인 요청에 성공하였습니다.";
        }

        return result;
    }
}