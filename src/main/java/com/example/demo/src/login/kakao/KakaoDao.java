package com.example.demo.src.login.kakao;

import com.example.demo.src.login.kakao.model.PostKakaoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class KakaoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createUser(PostKakaoUser postKakaoUser) {
        String createUserQuery = "insert into KakaoUser (name, birthday) VALUES (?)";
        Object[] createUserParams = new Object[]{postKakaoUser.getName(), postKakaoUser.getBirthday()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

    }

    public int checkUser(String kakaoUserName) {
        String checkUserIdxQuery = "select exists(select name from KakaoUser where name = ?)";
        String checkUserParam = kakaoUserName;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery, int.class,checkUserParam);
    }
}
