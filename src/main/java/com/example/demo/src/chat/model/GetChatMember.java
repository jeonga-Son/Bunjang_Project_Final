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
public class GetChatMember {
    private int chatMemberIdx;
    private int chatRoomIdx;
    private int userIdx1;
    private int userIdx2;
    private Timestamp updateAt;
    private String status;
}
