package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {
    private String name;

    private String phoneNo;

    @JsonFormat(pattern = "yyMMdd", timezone = "Asia/Seoul")
    private Date birthday;

}