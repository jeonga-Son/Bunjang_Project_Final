package com.example.demo.src.review;
import com.example.demo.src.review.model.PostReviewReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertReview(int productIdx, int userIdx, PostReviewReq postReviewReq) {
        String insertReviewQuery = "insert into Review(productIdx, userIdx, sellerIdx, star, content, reviewImgUrl)" +
                "values(?,?,?,?,?,?)";
        int sellerIdx = getSellerIdx(productIdx);

        Object[] insertReviewParams = new Object[]{
                // 유저 id, 별점, 리뷰내용, 리뷰이미지 url
                productIdx,
                userIdx,
                sellerIdx,
                postReviewReq.getStar(),
                postReviewReq.getContent(),
                postReviewReq.getReviewImgUrl()};

        return this.jdbcTemplate.update(insertReviewQuery, insertReviewParams);

    }

    public int getSellerIdx (int productIdx) {
        String getSellerIdxQuery = "select userIdx from Product where productIdx = ?;";
        int getSellerIdxParams = productIdx;

        return this.jdbcTemplate.queryForObject(getSellerIdxQuery, int.class, getSellerIdxParams);
    }
}


