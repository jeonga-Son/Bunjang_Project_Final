package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChat;
import com.example.demo.src.chat.model.ChatPartnerStore;
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
                "FROM (select Chat.chatRoomIdx, userIdx1, userIdx2, profileImgUrl, name, Chat.updateAt, message as lastMessage\n" +
                "      from ChatRoom\n" +
                "               left join User on ChatRoom.userIdx2=User.userIdx\n" +
                "               left join Chat on Chat.chatRoomIdx=ChatRoom.chatRoomIdx\n" +
                "      where ChatRoom.userIdx1=?\n" +
                "      order by Chat.updateAt desc\n" +
                "     ) as ordered_chat\n" +
                "GROUP BY ordered_chat.chatRoomIdx)\n" +
                "UNION\n" +
                "(SELECT *\n" +
                "FROM (select Chat.chatRoomIdx, userIdx1, userIdx2, profileImgUrl, name, Chat.updateAt, message as lastMessage\n" +
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
                        rs.getString("lastMessage"),
                        rs.getTimestamp("updateAt")),
                searchChatRoomsByUserIdxParams);
    }

    public List<ChatPartnerStore> getChat(int chatRoomIdx) {
        String getChatQuery = "select User.name, AVG(Review.star) as avgStar,\n" +
                "        (select count(Product.saleStatus)\n" +
                "            from Product\n" +
                "                left join User on User.userIdx = Product.userIdx\n" +
                "                left join ChatRoom on ChatRoom.userIdx2 = User.userIdx\n" +
                "                 where Product.saleStatus ='SOLD' and ChatRoom.chatRoomIdx = ?) as saleCount\n" +
                "from Chat\n" +
                "    left join ChatRoom on Chat.chatRoomIdx = ChatRoom.chatRoomIdx\n" +
                "    left join User on User.userIdx = ChatRoom.userIdx2\n" +
                "    left join Review on User.userIdx = Review.userIdx\n" +
                "    left join Product on User.userIdx = Product.userIdx\n" +
                "    where ChatRoom.chatRoomIdx = ? and Chat.userIdx = User.userIdx and User.status = 'ACTIVE' and ChatRoom.status = 'ACTIVE';";
        Object[] getChatParams = new Object[]{chatRoomIdx, chatRoomIdx};

        return this.jdbcTemplate.query(getChatQuery,
                (rs, rowNum) -> new ChatPartnerStore(
                        rs.getString("name"),
                        rs.getFloat("avgStar"),
                        rs.getInt("saleCount"),
                        this.jdbcTemplate.query("(select ChatRoom.userIdx1 as userIdx,  Chat.message, Chat.updateAt\n" +
                                        "                from Chat\n" +
                                        "                    left join ChatRoom on Chat.chatRoomIdx = ChatRoom.chatRoomIdx\n" +
                                        "                    left join User on User.userIdx = ChatRoom.userIdx1\n" +
                                        "                    left join Review on User.userIdx = Review.userIdx\n" +
                                        "                    left join Product on User.userIdx = Product.userIdx\n" +
                                        "                    where ChatRoom.chatRoomIdx = ? and Chat.userIdx = User.userIdx and User.status = 'ACTIVE' and ChatRoom.status = 'ACTIVE')\n" +
                                        "union\n" +
                                        "(select ChatRoom.userIdx2 as userIdx, Chat.message, Chat.updateAt\n" +
                                        "from Chat\n" +
                                        "    left join ChatRoom on Chat.chatRoomIdx = ChatRoom.chatRoomIdx\n" +
                                        "    left join User on User.userIdx = ChatRoom.userIdx2\n" +
                                        "    left join Review on User.userIdx = Review.userIdx\n" +
                                        "    left join Product on User.userIdx = Product.userIdx\n" +
                                        "    where ChatRoom.chatRoomIdx = ? and Chat.userIdx = User.userIdx and User.status = 'ACTIVE' and ChatRoom.status = 'ACTIVE');",
                                (rs2, rowNum2) -> new GetChat(
                                        rs2.getInt("userIdx"),
                                        rs2.getString("message"),
                                        rs2.getTimestamp("updateAt")),
                                getChatParams))
                , getChatParams);
    }

//    public List<GetChat> getChat(int chatRoomIdx) {
//        String getChatQuery = "(select User.name, AVG(Review.star) as avgStar,\n" +
//                "         (select count(Product.saleStatus)\n" +
//                "            from Product\n" +
//                "                left join User on User.userIdx = Product.userIdx\n" +
//                "                left join ChatRoom on ChatRoom.userIdx1 = User.userIdx\n" +
//                "                 where Product.saleStatus ='SOLD' and ChatRoom.chatRoomIdx = ?) as saleCount,\n" +
//                "        ChatRoom.userIdx1 as userIdx,  Chat.message, Chat.updateAt\n" +
//                "from Chat\n" +
//                "    left join ChatRoom on Chat.chatRoomIdx = ChatRoom.chatRoomIdx\n" +
//                "    left join User on User.userIdx = ChatRoom.userIdx1\n" +
//                "    left join Review on User.userIdx = Review.userIdx\n" +
//                "    left join Product on User.userIdx = Product.userIdx\n" +
//                "    where ChatRoom.chatRoomIdx = ? and Chat.userIdx = User.userIdx and User.status = 'ACTIVE' and ChatRoom.status = 'ACTIVE')\n" +
//                "union\n" +
//                "(select User.name, AVG(Review.star),\n" +
//                "        (select count(Product.saleStatus)\n" +
//                "            from Product\n" +
//                "                left join User on User.userIdx = Product.userIdx\n" +
//                "                left join ChatRoom on ChatRoom.userIdx2 = User.userIdx\n" +
//                "                 where Product.saleStatus ='SOLD' and ChatRoom.chatRoomIdx = ?) as saleCount,\n" +
//                "        ChatRoom.userIdx2 as userIdx, Chat.message, Chat.updateAt\n" +
//                "from Chat\n" +
//                "    left join ChatRoom on Chat.chatRoomIdx = ChatRoom.chatRoomIdx\n" +
//                "    left join User on User.userIdx = ChatRoom.userIdx2\n" +
//                "    left join Review on User.userIdx = Review.userIdx\n" +
//                "    left join Product on User.userIdx = Product.userIdx\n" +
//                "    where ChatRoom.chatRoomIdx = ? and Chat.userIdx = User.userIdx and User.status = 'ACTIVE' and ChatRoom.status = 'ACTIVE');";
//        Object[] getChatParams = new Object[]{chatRoomIdx, chatRoomIdx, chatRoomIdx, chatRoomIdx};
//
//        return this.jdbcTemplate.query(getChatQuery,
//                (rs, rowNum) -> new GetChat(
//                        rs.getString("name"),
//                        rs.getFloat("avgStar"),
//                        rs.getInt("saleCount"),
//                        rs.getInt("userIdx"),
//                        rs.getString("message"),
//                        rs.getTimestamp("updateAt")
//                ), getChatParams);
//    }

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

        String getChatPatchQuery = "update Chat set status = 'DELETED' where chatRoomIdx = ?;";
        this.jdbcTemplate.update(getChatPatchQuery, chatRoomIdx);

        String getChatMemberPatchQuery = "update ChatMember set status = 'DELETED' where chatRoomIdx = ?;";
        this.jdbcTemplate.update(getChatMemberPatchQuery, chatRoomIdx);
    }
}


