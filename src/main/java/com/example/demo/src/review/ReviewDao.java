package com.example.demo.src.review;

import com.example.demo.src.review.model.GetReviews;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.src.review.model.PostReviewReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Locale;


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


        this.jdbcTemplate.update(insertReviewQuery, insertReviewParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);


    }

    public int getSellerIdx (int productIdx) {
        String getSellerIdxQuery = "select userIdx from Product where productIdx = ?;";
        int getSellerIdxParams = productIdx;

        return this.jdbcTemplate.queryForObject(getSellerIdxQuery, int.class, getSellerIdxParams);
    }

    public int updateReviewStatus(int reviewIdx) {
        String updateReviewStatusQuery = "update Review set status = 'DELETED' where reviewIdx=?";
        int updateReviewStatusParams = reviewIdx;

        return this.jdbcTemplate.update(updateReviewStatusQuery, updateReviewStatusParams);

    }

    public GetReviewsRes getReviews(int userIdx) {
        String getReviewsQuery="select Review.userIdx, star, name, content, reviewImgUrl, Review.createAt, Review.productIdx, productName\n" +
                "from Review\n" +
                "    left join Product on Review.productIdx=Product.productIdx\n" +
                "    left join User on Review.userIdx=User.userIdx\n" +
                "where sellerIdx=? and Review.status='ACTIVE'";
        int getReviewsParams = userIdx;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        return this.jdbcTemplate.queryForObject("select count(*) as reviewCount from Review where sellerIdx=? and status='ACTIVE'",
                (rs1, rownum1) -> new GetReviewsRes(
                        rs1.getInt("reviewCount"),
                this.jdbcTemplate.query(getReviewsQuery,
                (rs,rownum) -> new GetReviews(
                        // 작성자 id, 별점, 작성자 이름, 리뷰 내용, 리뷰 이미지, 리뷰 작성일, 거래 상품 id, 상품 이름
                        rs.getInt("Review.userIdx"),
                        rs.getFloat("star"),
                        rs.getString("name"),
                        rs.getString("content"),
                        rs.getString("reviewImgUrl"),
                        sdf.format(rs.getTimestamp("createAt")),
                        rs.getInt("productIdx"),
                        rs.getString("productName")), getReviewsParams)),  getReviewsParams);

    }

    public int checkReviewExists(int reviewIdx) {
        String checkReviewExistsQuery = "select exists(select reviewIdx from Review where reviewIdx = ? and status='ACTIVE')";
        int checkReviewExistsParams = reviewIdx;

        return this.jdbcTemplate.queryForObject(checkReviewExistsQuery, int.class, checkReviewExistsParams);
    }

    public int getUserIdxOfReview(int reviewIdx) {
        String getUserIdxOfReviewQuery = "select userIdx from Review where reviewIdx = ? and status='ACTIVE'";
        int getUserIdxOfReviewParams = reviewIdx;

        return this.jdbcTemplate.queryForObject(getUserIdxOfReviewQuery, int.class, getUserIdxOfReviewParams);
    }

    public String getProductStatus(int productIdx) {
        String getProductStatusQuery = "select saleStatus from Product where productIdx = ? and status='ACTIVE'";
        int getProductStatusParams = productIdx;

        return this.jdbcTemplate.queryForObject(getProductStatusQuery, String.class, getProductStatusParams);

    }

    public int isReviewWritten(int productIdx) {
        String isReviewWrittenQuery = "select EXISTS(select reviewIdx from Review where productIdx = ? and status='ACTIVE')";
        int isReviewWrittenParams = productIdx;
        try {
            return this.jdbcTemplate.queryForObject(isReviewWrittenQuery, int.class, isReviewWrittenParams);
        } catch (Exception exception) {
            return 0;
        }
    }

    // 존재하는 상품인지?
    public int checkProductExists(int productIdx){
        String checkProductExistsQuery = "select exists(select productIdx from Product where productIdx = ? and status='ACTIVE');";
        int checkProductExistsParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkProductExistsQuery, int.class, checkProductExistsParams);
    }


}


