package com.example.demo.src.chat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    // 유저 id, 채팅방 id, 프로필이미지url, 마지막 메시지, 수정일
    private int userIdx;

    private int chatRoomIdx;

    private String profileImgUrl;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp updateDate;
}
