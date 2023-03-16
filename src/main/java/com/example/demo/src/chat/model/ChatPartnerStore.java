package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatPartnerStore {
    private String name;

    private Float avgStar;

    private int saleCount;

    private List<GetChat> getChatList;
}
