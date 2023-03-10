package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChat;
import com.example.demo.src.chat.model.GetChatRoomList;
import com.example.demo.src.chat.model.PostChatReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetChatRoomList> getChatList(int userIdx) {
        // 프로필 이미지 url, 이름, 상점 소개
        String searchChatRoomsByUserIdxQuery = "select User.userIdx as chatPartner, Chat.chatRoomIdx, " +
                "User.profileImgUrl, User.shopDescription, Chat.updateAt from User " +
                "left join Chat on User.userIdx = Chat.chatIdx where Chat.userIdx = ?";

        int searchChatRoomsByUserIdxParams = userIdx;

        return this.jdbcTemplate.query(searchChatRoomsByUserIdxQuery,
                (rs, rowNum) -> new GetChatRoomList(
                        rs.getInt("chatPartner"),
                        rs.getInt("chatRoomIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription"),
                        rs.getTimestamp("updateAt")),
                searchChatRoomsByUserIdxParams);
    }

    public GetChat getChat(int chatRoomIdx) {
        String getChatQuery = "select Chat.chatIdx, Chat.chatRoomIdx, Chat.message, Chat.updateAt, Chat.status\n" +
                "                from Chat left join ChatRoom on ChatRoom.chatRoomIdx = Chat.chatRoomIdx where ChatRoom.chatRoomIdx = ?";
        int getChatParam = chatRoomIdx;

        return this.jdbcTemplate.queryForObject(getChatQuery,
                (rs, rowNum) -> new GetChat(
                        // 유저 id, 이름, 프로필이미지Url, 상점 설명, 포인트 잔액, 팔로워 id, 팔로잉 id
                        // 상품 id, 상품이름, 상품 가격,상품판매 상태, 상품 이미지 불러오기 List
                        rs.getInt("chatIdx"),
                        rs.getInt("chatRoomIdx"),
                        rs.getString("message"),
                        rs.getTimestamp("updateAt"),
                        rs.getString("status")
                ), getChatParam);
    }

    // post, insert into
    public int createChat(PostChatReq postChatReq, int userIdx) {
        String getChatQuery = "insert into Chat (userIdx, chatRoomIdx, message) values(?, ?, ?)";

        Object[] getChatParams = new Object[]{
                userIdx,
                postChatReq.getChatRoomIdx(),
                postChatReq.getMessage()};

        this.jdbcTemplate.update(getChatQuery, getChatParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }


    public void patchChat(int chatRoomIdx) {
        String getPatchQuery = "update ChatRoom set status = 'DELETED' where chatRoomIdx = ? ";

        this.jdbcTemplate.update(getPatchQuery, chatRoomIdx);
    }
}


