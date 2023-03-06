

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
    private int userIdx;

    private String name;

    private String phoneNo;

    private Date birthday;

    private String address;

    private Float latitude;

    private Float longitude;

    @JsonFormat(pattern = "yyyy.MM.dd'T'hh:mm", timezone = "Asia/Seoul")
    private Timestamp createAt;

    @JsonFormat(pattern = "yyyy.MM.dd'T'hh:mm", timezone = "Asia/Seoul")
    private Timestamp updateAt;

    private String status;

    private String profileImgUrl;

    private String shopDescription;

}