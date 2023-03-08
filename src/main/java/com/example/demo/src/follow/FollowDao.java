package com.example.demo.src.follow;
import com.example.demo.src.follow.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Repository
public class FollowDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetFollowingsRes> getFollowings(int userIdx) {
        String getFollowingsQuery = "SELECT\n" +
                "    user_list.userIdx,\n" +
                "    user_list.name,\n" +
                "    user_list.profileImgUrl,\n" +
                "    COUNT(distinct Follow.followIdx) as followerCount,\n" +
                "    COUNT(distinct Product.productIdx) as productCount\n" +
                "\n" +
                "FROM\n" +
                "    (select User.userIdx, profileImgUrl, User.name\n" +
                "    from Follow\n" +
                "        left join User on Follow.followingUserIdx=User.userIdx\n" +
                "    where followerUserIdx = ? and followStatus='ACTIVE'\n" +
                "    group by userIdx) user_list\n" +
                "        LEFT JOIN Follow ON user_list.userIdx = Follow.followingUserIdx\n" +
                "        left join Product ON user_list.userIdx = Product.userIdx\n" +
                "where Follow.followStatus='ACTIVE'\n" +
                "GROUP BY user_list.userIdx;";
        int getFollowingsParams = userIdx;

        return this.jdbcTemplate.query(getFollowingsQuery,
                (rs, rownum) -> new GetFollowingsRes(
                        // 유저 id, 유저 프로필 이미지 url, 유저 이름, 상품 개수, 팔로워 수, 상품 List
                        rs.getInt("userIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getInt("productCount"),
                        rs.getInt("followerCount"),
                        this.jdbcTemplate.query("select Product.productIdx, ProductImg.productImgUrl, price\n" +
                                        "from Product\n" +
                                        "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                                        "where Product.userIdx=? and Product.status='ACTIVE' and saleStatus='ONSALE'\n" +
                                        "group by Product.productIdx\n" +
                                        "limit 3",
                                (rs1, rowNum) -> new GetFollowingsProducts(
                                        // 상품 id, 상품 이미지 url, 가격
                                        rs1.getInt("productIdx"),
                                        rs1.getString("productImgUrl"),
                                        rs1.getInt("price")
                                ), rs.getInt("userIdx"))
                ), getFollowingsParams);
    }

    public int insertFollow (int followerIdx, int followingUserIdx) {
        String insertFollowQuery = "insert into Follow(followerUserIdx, followingUserIdx) values (?,?)";
        Object[] insertFollowParams = new Object[] {followerIdx, followingUserIdx};

        this.jdbcTemplate.update(insertFollowQuery, insertFollowParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 팔로워 id, 팔로잉 유저 id로 팔로우 아이디 찾기
    public int getFollowIdx (int followerIdx, int followingUserIdx) {
        String updateFollowStatusQuery = "select followIdx\n" +
                "from Follow\n" +
                "where followerUserIdx=? and followingUserIdx=? and followStatus = 'ACTIVE'";
        Object[] updateFollowStatusParams = new Object[] {followerIdx, followingUserIdx};

        return this.jdbcTemplate.queryForObject(updateFollowStatusQuery, int.class, updateFollowStatusParams);
    }

    public int updateFollowStatus (int followIdx) {
        String updateFollowStatusQuery = "update Follow set followStatus='INACTIVE' where followIdx=?";
        int updateFollowStatusParams = followIdx;

        return this.jdbcTemplate.update(updateFollowStatusQuery, updateFollowStatusParams);

    }

    public List<GetFollowersRes> getFollowers(int userIdx) {
        String getFollowersQuery = "SELECT\n" +
                "    user_list.userIdx,\n" +
                "    user_list.name,\n" +
                "    user_list.profileImgUrl,\n" +
                "    COUNT(distinct Follow.followIdx) as followerCount,\n" +
                "    COUNT(distinct Product.productIdx) as productCount\n" +
                "\n" +
                "FROM\n" +
                "    (select User.userIdx, profileImgUrl, User.name\n" +
                "     from Follow\n" +
                "              left join User on Follow.followerUserIdx=User.userIdx\n" +
                "     where followingUserIdx = ? and followStatus='ACTIVE'\n" +
                "     group by userIdx) user_list\n" +
                "        LEFT JOIN Follow ON user_list.userIdx = Follow.followingUserIdx\n" +
                "        left join Product ON user_list.userIdx = Product.userIdx\n" +
                "where Follow.followStatus='ACTIVE' and Product.saleStatus='ONSALE' and Product.status='ACTIVE'\n" +
                "GROUP BY user_list.userIdx;";
        int getFollowersParams = userIdx;

        return this.jdbcTemplate.query(getFollowersQuery,
                (rs, rownum) -> new GetFollowersRes(
                        // 팔로워 유저 id, 유저 이름, 유저 프로필 사진, 팔로워 수, 상품 수
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("followerCount"),
                        rs.getInt("productCount")), getFollowersParams);
    }



}
