package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.GetChatRoomList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/chats")
public class ChatController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChatProvider chatProvider;

    public ChatController(ChatProvider chatProvider) {
        this.chatProvider = chatProvider;
    }


    /** 채팅방 조회 API
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/chatList")
    public BaseResponse<List<GetChatRoomList>> getChatRoomList(@RequestParam("userIdx") int userIdx) {
        try{
            List<GetChatRoomList> getChatRoomList = chatProvider.getChatRoomsByUserIdx(userIdx);
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

    /** 채팅 등록 API
     *
     * @return
     */

    /** 채팅 삭제 API
     *
     * @param chatIdx
     * @return
     */
}
