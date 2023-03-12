package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchDeleteUserReq {
    private int userIdx;

    private String deleteReasonContent;

    private Timestamp updateAt = Timestamp.valueOf(LocalDateTime.now());

}