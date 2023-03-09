package com.example.demo.src.chat;

import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.chat.model.PostChatRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private final ChatDao chatDao;

    public ChatService(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    public PostChatRes createChat(PostChatReq postChatReq) {
         PostChatReq postChatReq1 = chatDao.createChat(postChatReq);
         PostChatRes postChatRes = new PostChatRes();
         postChatRes.setChatIdx(postChatReq1.getChatIdx());
         postChatRes.setCreateAt(postChatReq1.getCreateAt());
         postChatRes.setChatRoomIdx(postChatRes.getChatRoomIdx());
         postChatRes.setMessage(postChatRes.getMessage());
         postChatRes.setStatus(postChatRes.getStatus());

         return postChatRes;
    }

    public void patchChat(int chatIdx) {
        chatDao.patchChat(chatIdx);
    }
}
