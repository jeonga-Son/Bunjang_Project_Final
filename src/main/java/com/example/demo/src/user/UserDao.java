package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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
                        rs.getString("profileImgUrl"),
                        rs.getString("status"),
                        rs.getString("shopDescription")),
                getUserParams);
    }
    

//    public int createUser(PostUserReq postUserReq){
//        String createUserQuery = "insert into UserInfo (userName, ID, password, email) VALUES (?,?,?,?)";
//        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getId(), postUserReq.getPassword(), postUserReq.getEmail()};
//        this.jdbcTemplate.update(createUserQuery, createUserParams);
//
//        String lastInserIdQuery = "select last_insert_id()";
//        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
//    }
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
