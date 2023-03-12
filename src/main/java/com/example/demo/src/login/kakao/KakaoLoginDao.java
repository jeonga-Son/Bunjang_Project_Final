package com.example.demo.src.login.kakao;

import com.example.demo.src.login.kakao.model.KakaoUser;
import com.example.demo.src.login.kakao.model.PostKakaoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class KakaoLoginDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createUser(PostKakaoUser postKakaoUser) {
        String createUserQuery = "insert into KakaoUser (kakaoUserName, kakaoUserBirthday, createAt) VALUES (?,?,?)";
        Object[] createUserParams = new Object[]{postKakaoUser.getKakaoUserName(), postKakaoUser.getKakaoUserBirthday(), postKakaoUser.getCreateAt()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

    }

    public int checkUser(String kakaoUserName) {
        String checkUserIdxQuery = "select exists(select kakaoUserName from KakaoUser where kakaoUserName = ?)";        String createUserParam = kakaoUserName;
        String checkUserParam = kakaoUserName;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery, int.class,createUserParam);
    }
}
