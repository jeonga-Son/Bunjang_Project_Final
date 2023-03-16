package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Controller
@RequestMapping("/chats")
public class ChatController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChatProvider chatProvider;

    @Autowired
    private final ChatService chatService;

    @Autowired
    private final JwtService jwtService;

    public ChatController(ChatProvider chatProvider, ChatService chatService, JwtService jwtService) {
        this.chatProvider = chatProvider;
        this.chatService = chatService;
        this.jwtService = jwtService;
    }


    /** 채팅 목록 조회 API => 쿼리수정
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/chatList")
    public BaseResponse<List<GetChatRoomList>> getChatList(@RequestParam("userIdx") int userIdx) {

        try{
            List<GetChatRoomList> getChatRoomList = chatProvider.getChatList(userIdx);
            return new BaseResponse<>(getChatRoomList);

        } catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /** 채팅 내역 조회 API
     *
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/{chatRoomIdx}")
    public BaseResponse<List<ChatPartnerStore>> getChat(@PathVariable("chatRoomIdx") int chatRoomIdx, @RequestParam("userIdx") int userIdx) {
        try{
            List<ChatPartnerStore> getChat = chatProvider.getChat(chatRoomIdx, userIdx);
            return new BaseResponse<>(getChat);

        } catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /** 채팅 등록 API
     *
     * @return
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostChatRes> createChat(@RequestBody PostChatReq postChatReq, @RequestParam("userIdx") int userIdx) throws BaseException {
        // 채팅방 번호 입력 안할 시
        if(postChatReq.getChatRoomIdx() == 0) {
            return new BaseResponse<>(POST_EMPTY_CHATROOMIDX);
        }

        // 채팅 내용 입력 안할 시
        if(postChatReq.getMessage() == null) {
            return new BaseResponse<>(POST_EMPTY_CHAT_MESSAGE);
        }

        try{
            PostChatRes createChat = chatService.createChat(postChatReq, userIdx);
            return new BaseResponse<>(createChat);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /** 채팅 상태 변경 API (삭제)
     *
     * @param
     * @return
     */
    @ResponseBody
    @PatchMapping("/{chatRoomIdx}/status")
    public BaseResponse<PatchChatRes> deleteChat(@PathVariable("chatRoomIdx") int chatRoomIdx, @RequestParam("userIdx") int userIdx) {
        try{
            chatService.patchChat(chatRoomIdx, userIdx);
            PatchChatRes patchChatRes = new PatchChatRes();
            patchChatRes.setChatRoomIdx(chatRoomIdx);

            String result = "대화 내용이 모두 삭제됩니다.";

            patchChatRes.setResultMessage(result);

            return new BaseResponse<>(patchChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

}
