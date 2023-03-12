package com.example.demo.src.login.kakao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigInteger;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoProfile {

    public BigInteger id;
    public String connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;


    @Data
    public class Properties {
        public String nickname;
        public String profile_image;
        public String thumbnail_image;

    }

    @Data
    public class KakaoAccount {

        public Boolean profile_image_needs_agreement;
        public Boolean profile_nickname_needs_agreement;
        public Profile profile;
        public Boolean has_email;
        public Boolean email_needs_agreement;
        public Boolean is_email_valid;
        public Boolean is_email_verified;
        public String email;
        public Boolean birthday_needs_agreement;
        public String birthday;
        public Boolean has_birthday;
        public String birthday_type;

        @Data
        public class Profile {
            public String nickname;
            public String thumbnail_image_url;
            public String profile_image_url;
            public boolean is_default_image;

        }
    }
}