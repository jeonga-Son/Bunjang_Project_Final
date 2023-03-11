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
        String searchChatRoomsByUserIdxQuery = "(SELECT *\n" +
                "FROM (select Chat.chatRoomIdx, userIdx1, userIdx2, profileImgUrl, name, Chat.updateAt, message\n" +
                "      from ChatRoom\n" +
                "               left join User on ChatRoom.userIdx2=User.userIdx\n" +
                "               left join Chat on Chat.chatRoomIdx=ChatRoom.chatRoomIdx\n" +
                "      where ChatRoom.userIdx1=?\n" +
                "      order by Chat.updateAt desc\n" +
                "     ) as ordered_chat\n" +
                "GROUP BY ordered_chat.chatRoomIdx)\n" +
                "UNION\n" +
                "(SELECT *\n" +
                "FROM (select Chat.chatRoomIdx, userIdx1, userIdx2, profileImgUrl, name, Chat.updateAt, message\n" +
                "      from ChatRoom\n" +
                "               left join User on ChatRoom.userIdx1=User.userIdx\n" +
                "               left join Chat on Chat.chatRoomIdx=ChatRoom.chatRoomIdx\n" +
                "      where ChatRoom.userIdx2=?\n" +
                "      order by Chat.updateAt desc\n" +
                "     ) as ordered_chat\n" +
                "GROUP BY ordered_chat.chatRoomIdx)";

        Object[] searchChatRoomsByUserIdxParams = new Object[]{userIdx, userIdx};

        return this.jdbcTemplate.query(searchChatRoomsByUserIdxQuery,
                (rs, rowNum) -> new GetChatRoomList(
                        rs.getInt("userIdx1"),
                        rs.getInt("userIdx2"),
                        rs.getString("chatRoomIdx"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("message"),
                        rs.getTimestamp("updateAt")),
                searchChatRoomsByUserIdxParams);
    }

    public GetChat getChat(int chatRoomIdx) {
        String getChatQuery = "select Chat.chatIdx, Chat.chatRoomIdx, Chat.message, Chat.updateAt, Chat.status\n" +
                "                from Chat left join ChatRoom on ChatRoom.chatRoomIdx = Chat.chatRoomIdx where ChatRoom.chatRoomIdx = ?";
        int getChatParam = chatRoomIdx;

        return this.jdbcTemplate.queryForObject(getChatQuery,
                (rs, rowNum) -> new GetChat(
                        rs.getInt("chatIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("chatRoomIdx"),
                        rs.getInt("chatMemberIdx"),
                        rs.getString("message"),
                        rs.getTimestamp("updateAt"),
                        rs.getString("status")
                ), getChatParam);
    }

    // post, insert into
    public int createChat(PostChatReq postChatReq, int userIdx) {
        String getChatQuery = "insert into Chat (userIdx, chatRoomIdx, message, updateAt) values(?, ?, ?, ?)";

        Object[] getChatParams = new Object[]{
                userIdx,
                postChatReq.getChatRoomIdx(),
                postChatReq.getMessage(),
                postChatReq.getUpdateAt()};

        this.jdbcTemplate.update(getChatQuery, getChatParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }


    public void patchChat(int chatRoomIdx) {
        String getPatchQuery = "update ChatRoom set status = 'DELETED' where chatRoomIdx = ? ";

        this.jdbcTemplate.update(getPatchQuery, chatRoomIdx);
    }
}


