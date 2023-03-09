package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChatRoomList;
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

    public List<GetChatRoomList> searchChatRoomByUserIdx(int userIdx) {
        String searchByUserIdxQuery = "";
        int searchChatRoomByUserIdxParams = userIdx;

        return this.jdbcTemplate.query(searchByUserIdxQuery,
                (rs, rowNum) -> new GetChatRoomList());
    }
}
