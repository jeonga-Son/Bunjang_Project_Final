package com.example.demo.src.chat.model;

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
public class PostChatReq {
    private int chatRoomIdx;

    private String message;

    private Timestamp updateAt = Timestamp.valueOf(LocalDateTime.now());
}
