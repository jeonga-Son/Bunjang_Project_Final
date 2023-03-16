package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.chat.model.PostChatRes;
import com.example.demo.src.user.UserProvider;
import com.example.demo.utils.JwtService;
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
    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final JwtService jwtService;


    public ChatService(ChatDao chatDao, UserProvider userProvider, JwtService jwtService) {
        this.chatDao = chatDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }

    public PostChatRes createChat(PostChatReq postChatReq, int userIdx) throws BaseException {
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

    public void patchChat(int chatRoomIdx, int userIdx) throws BaseException {
        // 존재하는 채팅방인지 체크
        if (chatDao.checkChatRoomIdx(chatRoomIdx) == 0) {
            throw new BaseException(CHATROOM_NOT_EXISTS);
        }

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

        try{
            chatDao.patchChat(chatRoomIdx);
        } catch (Exception exception) {
            logger.error("App - patchChat Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
