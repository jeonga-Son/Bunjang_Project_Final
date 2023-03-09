package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
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

    public List<GetChatRoomList> getChatRoomsByUserIdx(int userIdx) throws BaseException {
        try {
            List<GetChatRoomList> getProductLists = chatDao.searchChatRoomByUserIdx(userIdx);
            return getProductLists;
        } catch (Exception exception) {
            logger.error("App - getProductLists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
