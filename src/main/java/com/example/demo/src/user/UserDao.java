package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select * from User where userIdx = ?";

        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("phoneNo"),
                        rs.getDate("birthday"),
                        rs.getString("address"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getTimestamp("createAt"),
                        rs.getTimestamp("updateAt"),
                        rs.getString("status"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription")),
                getUserParams);
    }

//    public User getUser(PostLoginReq postLoginReq) {
//        String getCheckQuery = "select userIdx, name, phoneNo, birthday from User where phoneNo = ?";
//        String getCheckParams = postLoginReq.getPhoneNo();
//
//        return this.jdbcTemplate.queryForObject(getCheckQuery,
//                (rs, rowNum) -> new User(
//                        rs.getInt("userIdx"),
//                        rs.getString("name"),
//                        rs.getString("phoneNo"),
//                        rs.getDate("birthday"),
//                        rs.getString("address"),
//                        rs.getFloat("latitude"),
//                        rs.getFloat("longitude"),
//                        rs.getTimestamp("createAt"),
//                        rs.getTimestamp("updateAt"),
//                        rs.getString("status"),
//                        rs.getString("profileImgUrl"),
//                        rs.getString("shopDescription")),
//                getCheckParams
//                );
//    }

    public User checkUser(PostLoginReq postLoginReq) {
        String checkUserQuery = "select userIdx, name, phoneNo, birthday, address, latitude," +
                "longitude, createAt, updateAt, status, profileImgUrl, shopDescription from User where phoneNo = ?";
        String checkUserParam = postLoginReq.getPhoneNo();
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("phoneNo"),
                        rs.getDate("birthday"),
                        rs.getString("address"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getTimestamp("createAt"),
                        rs.getTimestamp("updateAt"),
                        rs.getString("status"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription")),
                checkUserParam);
    }


    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, phoneNo, birthday) VALUES (?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getPhoneNo(), postUserReq.getBirthday()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkPhoneNo(PostLoginReq postLoginReq) {
        String checkUserIdxQuery = "select exists(select name, phoneNo from User where phoneNo = ?)";
        String checkPhoneNoParma = postLoginReq.getPhoneNo();

        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,int.class, checkPhoneNoParma);
    }

    public int deleteUser(int userIdx) {
        String deleteUserQuery = "update User set status = 'Deleted' where userIdx = ? ";

        return this.jdbcTemplate.update(deleteUserQuery,userIdx);
    }

    public int modifyShop(int userIdx, PatchShopInfoReq patchShopInfoReq) {
        String modifyShopQuery = "update User set profileImgUrl = ?, shopDescription = ?  where userIdx = ? ";
        Object[] modifyShopParams = new Object[]{patchShopInfoReq.getProfileImgUrl(), patchShopInfoReq.getShopDescription(), userIdx};
        return this.jdbcTemplate.update(modifyShopQuery, modifyShopParams);
    }

}
