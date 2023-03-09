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
public class GetChat {
    private int chatIdx;

    private int chatRoomIdx;

    private String message;

    private Timestamp createAt;

    private Timestamp updateAt;

    private String status;
}
