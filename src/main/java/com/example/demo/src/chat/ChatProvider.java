package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChat;
import com.example.demo.src.chat.model.GetChatRoomList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ChatProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChatDao chatDao;

    public ChatProvider(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    public List<GetChatRoomList> getChatList(int userIdx) throws BaseException {
        try {
            List<GetChatRoomList> getChatList = chatDao.getChatList(userIdx);
            return getChatList;
        } catch (Exception exception) {
            logger.error("App - getProductLists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetChat getChat(int chatIdx) throws BaseException {
        try {
            GetChat getChat = chatDao.getChat(chatIdx);
            return getChat;
        } catch (Exception exception) {
            logger.error("App - getProductLists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
