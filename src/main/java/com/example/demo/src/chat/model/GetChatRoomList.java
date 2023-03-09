package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomList {
    // 채팅방 목록 가져오기
    // 유저 id, 채팅방 id, 프로필이미지url, 상점 소개, 생성일, 수정일
    private int userIdx;

    private int chatRoomIdx;

    private String profileImgUrl;

    private String shopDescription;

    private Timestamp createDate;

    private Timestamp updateDate;
}
