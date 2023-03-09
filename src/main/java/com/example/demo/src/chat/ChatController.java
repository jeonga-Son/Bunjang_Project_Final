package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.*;
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

    public ChatController(ChatProvider chatProvider, ChatService chatService) {
        this.chatProvider = chatProvider;
        this.chatService = chatService;
    }


    /** 채팅방 조회 API
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

    /** 채팅방 내역 조회 API
     *
     * @param chatIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{chatIdx}}")
    public BaseResponse<GetChat> getChat(@PathVariable ("{chatIdx}") int chatIdx) {
        try{
            GetChat getChat = chatProvider.getChat(chatIdx);
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
    @GetMapping("")
    public BaseResponse<PostChatRes> createChat(@RequestBody PostChatReq postChatReq) {
        PostChatRes createChat = chatService.createChat(postChatReq);
        return new BaseResponse<>(createChat);

    }

    /** 채팅 삭제 API
     *
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/{chatIdx}/status")
    public BaseResponse<String> deleteChat(@PathVariable("{chatIdx}") int chatIdx) {
        chatService.patchChat(chatIdx);
        String result = "채팅방을 나가기를 완료하였습니다..";
        return new BaseResponse<>(result);

    }

}
