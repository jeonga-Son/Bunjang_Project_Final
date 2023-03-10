//package com.example.demo.src.favorite;
//
//import com.example.demo.src.review.model.GetReviews;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//
//
//@Repository
//public class FavoriteDao {
//
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public void setDataSource(DataSource dataSource){
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }
//
//    public int insertFavorite(int productIdx, int userIdx) {
//        String insertReviewQuery = "insert into Favorite(productIdx, userIdx, sellerIdx, star, content, reviewImgUrl)" +
//                "values(?,?,?,?,?,?)";
//        int sellerIdx = getSellerIdx(productIdx);
//
//        Object[] insertReviewParams = new Object[]{
//                // 유저 id, 별점, 리뷰내용, 리뷰이미지 url
//                productIdx,
//                userIdx,
//                sellerIdx,
//                postReviewReq.getStar(),
//                postReviewReq.getContent(),
//                postReviewReq.getReviewImgUrl()};
//
//
//        return this.jdbcTemplate.update(insertReviewQuery, insertReviewParams);
//
//    }
//
//}
//
//
