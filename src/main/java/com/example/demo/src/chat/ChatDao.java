package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChat;
import com.example.demo.src.chat.model.GetChatRoomList;
import com.example.demo.src.chat.model.PatchChatRes;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.product.model.GetProductList;
import com.example.demo.src.user.model.GetMyPageRes;
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
        String searchChatRoomsByUserIdxQuery = "select User.userIdx, User.name, User.shopDescription, User.profileImgUrl\n" +
                "from User\n" +
                "    where User.userIdx = any(select ChatMember.userIdx2\n" +
                "    from ChatMember\n" +
                "        left join User on User.userIdx = ChatMember.userIdx1\n" +
                "        left join Chat on Chat.userIdx = User.userIdx\n" +
                "        where User.userIdx = ? and ChatMember.status = 'ACTIVE');";

        int searchChatRoomsByUserIdxParams = userIdx;

        return this.jdbcTemplate.query(searchChatRoomsByUserIdxQuery,
                (rs, rowNum) -> new GetChatRoomList(
                        rs.getInt("userIdx"),
                        rs.getInt("RoomIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription"),
                        rs.getTimestamp("createDate"),
                        rs.getTimestamp("updateDate")),
                searchChatRoomsByUserIdxParams);
    }

    public GetChat getChat(int chatIdx) {
        String getChatQuery = "";
        Object[] getChatParams = new Object[]{};

        return this.jdbcTemplate.queryForObject(getChatQuery,
                (rs, rowNum) -> new GetMyPageRes(
                        // 유저 id, 이름, 프로필이미지Url, 상점 설명, 포인트 잔액, 팔로워 id, 팔로잉 id
                        // 상품 id, 상품이름, 상품 가격,상품판매 상태, 상품 이미지 불러오기 List
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getFloat("avgStar"),
                        rs.getInt("point"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        this.jdbcTemplate.query("select Product.productIdx, Product.price, Product.productName, ProductImg.productImgUrl\n" +
                                        "    from Product\n" +
                                        "        left join ProductImg on Product.productIdx = ProductImg.productIdx\n" +
                                        "        where Product.userIdx = ? and ProductImg.status='ACTIVE' and Product.status='ACTIVE' and Product.saleStatus = 'ONSALE'\n" +
                                        "Group by Product.productIdx;",
                                (rs2, rowNum2) -> new GetProductList(
                                        rs2.getInt("productIdx"),
                                        rs2.getString("productImgUrl"),
                                        rs2.getInt("price"),
                                        rs2.getString("productName")),
                                rs.getInt("userIdx"))
                ), getChatParams);
    }

    // post, insert into
    public PostChatReq createChat(PostChatReq postChatReq) {
        String getChatQuery = "";
        Object[] getChatParams = new Object[]{};

        return this.jdbcTemplate.queryForObject(getChatQuery,

                ), getChatParams);
    }

    public void patchChat(int chatIdx) {
        String getPatchQuery = "";
        Object[] getChatParams = new Object[]{};

        return this.jdbcTemplate.queryForObject(getChatQuery,

                ), getChatParams);
    }
}


