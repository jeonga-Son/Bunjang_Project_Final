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
//     */
    @ResponseBody
    @GetMapping("/{chatRoomIdx}")
    public BaseResponse<List<GetChat>> getChat(@PathVariable("chatRoomIdx") int chatRoomIdx) {
        try{
            List<GetChat> getChat = chatProvider.getChat(chatRoomIdx);
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
    public BaseResponse<String> deleteChat(@PathVariable("chatRoomIdx") int chatRoomIdx) {
        chatService.patchChat(chatRoomIdx);
        String result = "대화 내용이 모두 삭제됩니다.";
        return new BaseResponse<>(result);

    }

}
