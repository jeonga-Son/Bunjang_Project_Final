package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.chat.model.PostChatRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ChatService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChatDao chatDao;

    public ChatService(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    public PostChatRes createChat(PostChatReq postChatReq, int userIdx) throws BaseException {
         try{
             int idx = chatDao.createChat(postChatReq, userIdx);

             if (idx == 0) {
                 throw new BaseException(FAILED_TO_CREATE_CHAT);
             }

             int chatRoomIdx = postChatReq.getChatRoomIdx();
             String message = postChatReq.getMessage();
             Timestamp updateAt = postChatReq.getUpdateAt();

             return new PostChatRes(chatRoomIdx, message, updateAt) ;

         }catch (Exception exception) {
             logger.error("App - createChat Service Error", exception);
             throw new BaseException(DATABASE_ERROR);
         }
    }

    public void patchChat(int chatRoomIdx) throws BaseException {
        // 존재하는 채팅방인지 체크
        if (chatDao.checkChatRoomIdx(chatRoomIdx) == 0) {
            throw new BaseException(CHATROOM_NOT_EXISTS);
        }

        try{
            chatDao.patchChat(chatRoomIdx);
        } catch (Exception exception) {
            logger.error("App - patchChat Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
