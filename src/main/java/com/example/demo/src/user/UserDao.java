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

//    public User getCheck(PostLoginReq postLoginReq) {
//        String getCheckQuery = "select name, phoneNo, birthday from User where userIdx = ?";
//        String getCheckParams = postLoginReq.getPhoneNo();
//
//        return this.jdbcTemplate.queryForObject(getCheckQuery,
//                (rs, rowNum) -> new User(
//                        rs.getString("name"),
//                        rs.getString("phoneNo"),
//                        rs.getDate("birthday")),
//                getCheckParams
//                );
//    }

    public PostLoginRes checkUser(PostLoginReq postLoginReq) {
        String checkUserQuery = "select exists(select name, phoneNo from User where phoneNo = ?)";
        String checkUserParam = postLoginReq.getPhoneNo();
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                (rs, rowNum) -> new PostLoginRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("jwt")),
                checkUserParam);
    }


    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, phoneNo, birthday) VALUES (?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getPhoneNo(), postUserReq.getBirthday()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkPhoneNo(String phoneNo) {
        String checkUserIdxQuery = "select exists(select phoneNo from User where phoneNo = ?)";
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                phoneNo);
    }

//    public void deleteUser(int userIdx) {
//        String deleteUserQuery = "update User set status = ? where userIdx = ? ";
//        Object[] deleteUserParams = new Object[]{patchUserReq.getUserName(), pa.getUserIdx()};
//
//        this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
//    }

    public int deleteUser(int userIdx) {
        String deleteUserQuery = "update User set status = 'Deleted' where userIdx = ? ";

        return this.jdbcTemplate.update(deleteUserQuery,userIdx);
    }

    public int modifyShop(int userIdx, PatchShopInfoReq patchShopInfoReq) {
        String modifyShopQuery = "update User set profileImgUrl = ?, shopDescription = ?  where userIdx = ? ";
        Object[] modifyShopParams = new Object[]{patchShopInfoReq.getProfileImgUrl(), patchShopInfoReq.getShopDescription(), userIdx};
        return this.jdbcTemplate.update(modifyShopQuery, modifyShopParams);
    }


//
//    public int checkEmail(String email){
//        String checkEmailQuery = "select exists(select email from UserInfo where email = ?)";
//        String checkEmailParams = email;
//        return this.jdbcTemplate.queryForObject(checkEmailQuery,
//                int.class,
//                checkEmailParams);
//
//    }
//
//    public int modifyUserName(PatchUserReq patchUserReq){
//        String modifyUserNameQuery = "update UserInfo set userName = ? where userIdx = ? ";
//        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};
//
//        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
//    }
//
//    public User getPwd(PostLoginReq postLoginReq){
//        String getPwdQuery = "select userIdx, password,email,userName,ID from UserInfo where ID = ?";
//        String getPwdParams = postLoginReq.getId();
//
//        return this.jdbcTemplate.queryForObject(getPwdQuery,
//                (rs,rowNum)-> new User(
//                        rs.getInt("userIdx"),
//                        rs.getString("ID"),
//                        rs.getString("userName"),
//                        rs.getString("password"),
//                        rs.getString("email")
//                ),
//                getPwdParams
//                );
//
//    }


}
