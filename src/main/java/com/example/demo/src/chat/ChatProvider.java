package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChatRoomList;
import com.example.demo.src.chat.model.ChatPartnerStore;
import com.example.demo.src.user.UserProvider;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ChatProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChatDao chatDao;
    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final JwtService jwtService;

    public ChatProvider(ChatDao chatDao, UserProvider userProvider, JwtService jwtService) {
        this.chatDao = chatDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }

    // 채팅 목록 조회
    public List<GetChatRoomList> getChatList(int userIdx) throws BaseException {
        // 존재하는 유저(=상점)인지 체크
        if (userProvider.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_EXISTS);
        }

        // 회원용 API
        // jwt에서 userIdx 추출
        int userIdxByJwt = jwtService.getUserIdx();

        // 유저(=상점)의 userIdx != jwt에서 추출한 userIdx
        if (userIdx != userIdxByJwt) {
            throw new BaseException(INVALID_USER_JWT);
        }

        // 채팅방, 채팅, 유저, 채팅 멤버 상태가 'ACTIVE' 인지 체크
        if (chatDao.checkChatStatus(userIdx) == 0) {
            throw new BaseException(CHATS_NOT_EXISTS);
        }

        try {
            List<GetChatRoomList> getChatList = chatDao.getChatList(userIdx);
            return getChatList;
        } catch (Exception exception) {
            logger.error("App - getChatList Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<ChatPartnerStore> getChat(int chatRoomIdx) throws BaseException {
        // 존재하는 유저(=상점)인지 체크
//        if (userProvider.checkUserIdx(userIdx) == 0) {
//            throw new BaseException(USERS_NOT_EXISTS);
//        }
//
//        // 회원용 API
//        // jwt에서 userIdx 추출
//        int userIdxByJwt = jwtService.getUserIdx();
//
//        // 유저(=상점)의 userIdx != jwt에서 추출한 userIdx
//        if (userIdx != userIdxByJwt) {
//            throw new BaseException(INVALID_USER_JWT);
//        }
//
//        // 채팅방, 채팅, 유저, 채팅 멤버 상태가 'ACTIVE' 인지 체크
//        if (chatDao.checkChatStatus(userIdx) == 0) {
//            throw new BaseException(CHATS_NOT_EXISTS);
//        }

        try {
            List<ChatPartnerStore> getChat = chatDao.getChat(chatRoomIdx);
            return getChat;
        } catch (Exception exception) {
            logger.error("App - getChat Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
