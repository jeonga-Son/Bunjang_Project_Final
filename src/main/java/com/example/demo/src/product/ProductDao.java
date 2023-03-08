package com.example.demo.src.product;
import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Repository
public class ProductDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getUserIdxByProductIdx(int productIdx) {
        String getUserIdxQuery = "select User.userIdx\n" +
                "from User\n" +
                "    left join Product on User.userIdx=Product.userIdx\n" +
                "where productIdx=?;";
        int getProductIdxParams = productIdx;

        return this.jdbcTemplate.queryForObject(getUserIdxQuery, int.class, getProductIdxParams);
    }
    public List<GetReviewList> getReviews(int userIdx){
        String getReviewsQuery = "select reviewIdx, star, content, reviewImgUrl, User.name as userName, Review.createAt\n" +
                "from Review\n" +
                "    left join User on Review.sellerIdx=User.userIdx\n" +
                "where Review.sellerIdx=?\n" +
                "limit 2;";

        int getReviewsParams = userIdx;

        return this.jdbcTemplate.query(getReviewsQuery,
                (rs, rowNum) -> new GetReviewList (
                        // 후기 List (2개까지 출력, 리뷰id, 후기 별점, 후기 내용, 후기 사진, 후기 작성자 이름, 작성일자)
                        rs.getInt("reviewIdx"),
                        rs.getInt("star"),
                        rs.getString("content"),
                        rs.getString("reviewImgUrl"),
                        rs.getString("userName"),
                        rs.getString("createAt")),
                        getReviewsParams);
    }

    public List<GetReviewList> getReviews(int userIdx, int limit){
        String getReviewsQuery = "select reviewIdx, star, content, reviewImgUrl, Review.userIdx, Review.createAt\n" +
                "from Review left join User on Review.sellerIdx=User.userIdx\n" +
                "where Review.sellerIdx=?\n" +
                "limit ?;";

        Object[] getReviewsParams = new Object[] {userIdx, limit};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        return this.jdbcTemplate.query(getReviewsQuery,
                (rs, rowNum) -> new GetReviewList (
                        // 후기 List (2개까지 출력, 리뷰id, 후기 별점, 후기 내용, 후기 사진, 후기 작성자 이름, 작성일자)
                        rs.getInt("reviewidx"),
                        rs.getInt("star"),
                        rs.getString("content"),
                        rs.getString("reviewImgUrl"),
                        rs.getString("userName"),
                        sdf.format(rs.getString("createAt"))),
                getReviewsParams);
    }

    public List<GetProductList> getProducts(int userIdx) {
        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName\n" +
                "from User\n" +
                "    left join Product on User.userIdx = Product.userIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where User.userIdx=?\n" +
                "group by Product.productIdx;" +
                "limit 6";
        int getProductsParams = userIdx;

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName")),
                getProductsParams
                );
    }


    public List<GetProductList> getProducts(int userIdx, int limit) {
        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName\n" +
                "from User\n" +
                "    left join Product on User.userIdx = Product.userIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where User.userIdx=?\n" +
                "group by Product.productIdx" +
                "limit ?";
        Object[] getProductsParams = new Object[] {userIdx, limit};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName")),
                getProductsParams
        );
    }

    public GetShopInfo getShopInfo(int userIdx) {
        String getShopInfo = "select User.userIdx, name, round(avg(Review.star),1) as avgStar,\n" +
                "    (select count(Follow.followIdx) from User left join Follow on User.userIdx=Follow.followingUserIdx where User.userIdx=?) as followerCount,\n" +
                "    (select count(Product.productIdx) from User left join Product on User.userIdx=Product.userIdx where User.userIdx=? and saleStatus='ONSALE') as productCount,\n" +
                "    (select count(Review.reviewIdx) from User left join Review on User.userIdx=Review.sellerIdx where User.userIdx=?) as reviewCount\n" +
                "from User\n" +
                "    left join Review on User.userIdx=Review.sellerIdx\n" +
                "where User.userIdx=?;";
        Object[] getShopInfoParams = new Object[] {userIdx, userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(getShopInfo,
                (rs, rownum) -> new GetShopInfo(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getFloat("avgStar"),
                        rs.getInt("followerCount"),
                        rs.getInt("productCount"),
                        rs.getInt("reviewCount")
                ), getShopInfoParams);
    }

    public GetProductInfoRes getProductInfoRes(int productIdx) {
        String getProductQuery = "select Product.productIdx, price, productName, Product.createAt as date, saleStatus,\n" +
                "       SubCategory.subCategoryIdx, SubCategory.subCategoryName, Product.userIdx,\n" +
                "       (select count(chatRoomIdx)\n" +
                "        from Product left join ChatRoom on Product.productIdx=ChatRoom.productIdx\n" +
                "        where Product.productIdx=?) as chatCount,\n" +
                "       (select count(favoriteIdx)\n" +
                "        from Product left join Favorite on Product.productIdx=Favorite.productIdx\n" +
                "        where Product.productIdx=?) as favoriteCount\n" +
                "from Product\n" +
                "    left join User on Product.userIdx=User.userIdx\n" +
                "    left join SubCategory on Product.subCategoryIdx=SubCategory.subCategoryIdx\n" +
                "where Product.productIdx = ?\n" +
                "group by Product.productIdx;";
        Object[] getProductParams = new Object[] {productIdx, productIdx, productIdx};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        return this.jdbcTemplate.queryForObject(getProductQuery,
                (rs, rownum) -> new GetProductInfoRes(
                        // 상품 id, 금액, 상품 이름, 게시일, 판매상태, 서브 카테고리 id, 서브 카테고리 이름, 작성자 id, 채팅 수, 찜 수
                        // 상품 이미지 불러오기 List
                        // 키워드 불러오기 List
                        rs.getInt("productIdx"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        sdf.format(rs.getTimestamp("date")),
                        rs.getString("saleStatus"),
                        rs.getInt("subCategoryIdx"),
                        rs.getString("subCategoryName"),
                        rs.getInt("userIdx"),
                        rs.getInt("chatCount"),
                        rs.getInt("favoriteCount"),
                        this.jdbcTemplate.query("select productImgUrl\n" +
                                        "from Product\n" +
                                        "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                                        "where Product.productIdx = ? and ProductImgUrl.status='ACTIVE';",
                                (rs1, rowNum) -> new PostProductImgs(
                                        rs1.getString("productImgUrl")),
                                rs.getInt("productIdx")),
                        this.jdbcTemplate.query("select tag\n" +
                                "from Tag\n" +
                                "where productIdx = ? and tag.status='ACTIVE';",
                                (rs2,rowNum1) -> new PostTags(
                                        rs2.getString("tag")
                                ), rs.getInt("productIdx"))
                ), getProductParams);

    }

    public int insertProducts(int userIdx, PostProductReq postProductReq){
        String insertProductsQuery = "insert into Product(categoryIdx, subCategoryidx, userIdx, productName, price, description) values(?,?,?,?,?,?)";
        Object [] insertProductsParams = new Object[] {
                postProductReq.getCategoryIdx(),
                postProductReq.getSubCategoryIdx(),
                userIdx,
                postProductReq.getProductName(),
                postProductReq.getPrice(),
                postProductReq.getDescription()
        };

        this.jdbcTemplate.update(insertProductsQuery, insertProductsParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int insertProductImgs(int productIdx, PostProductImgs postProductImgs) {
        String insertProductImgsQuery = "insert into ProductImg(productIdx, productImgUrl) values(?,?);";
        Object[] insertProductImgsParams = new Object[]{productIdx, postProductImgs.getProductImgUrl()};
        return this.jdbcTemplate.update(insertProductImgsQuery, insertProductImgsParams);

    }

    public int insertTags(int productIdx, PostTags postTags) {
        String insertTagsQuery = "insert into Tag(productIdx, tag) values(?,?);";
        Object[] insertTagsParams = new Object[]{productIdx, postTags.getTag()};

        return this.jdbcTemplate.update(insertTagsQuery, insertTagsParams);
    }

    public int updateProduct(int productIdx, PatchProductReq patchProductReq) {
        // Product Table은 update만
        String updatePruductQuery = "update Product\n" +
                "set categoryIdx = ?, subCategoryIdx = ?, productName =?, price=?,description=?\n" +
                "where productIdx=?";

        Object[] updatePruductParams = new Object[] {
                patchProductReq.getCategoryIdx(),
                patchProductReq.getSubCategoryIdx(),
                patchProductReq.getProductName(),
                patchProductReq.getPrice(),
                patchProductReq.getDescription(),
                productIdx};

        return this.jdbcTemplate.update(updatePruductQuery, updatePruductParams);


    }

    public void deleteProductImgs (int productIdx) {
        String updateProductImgQuery="update ProductImg set status = 'DELETED' where productIdx =?";

        int deletePruductImgsParams = productIdx;
        this.jdbcTemplate.update(updateProductImgQuery, deletePruductImgsParams);

    }

    public void deleteProductTags (int productIdx) {
        String updateProductImgQuery="update Tag set status = 'DELETED' where productIdx =?";

        int deletePruductTagsParams = productIdx;
        this.jdbcTemplate.update(updateProductImgQuery, deletePruductTagsParams);

    }

    public int updateSaleStatus (int productIdx, String saleStatus) {
        String updateSaleStatusQuery = "update Product set saleStatus = ? where productIdx=?";
        Object[] updateSaleStatusParams = new Object [] {saleStatus, productIdx};

        return this.jdbcTemplate.update(updateSaleStatusQuery, updateSaleStatusParams);
    }

    public int updateProductStatus (int productIdx) {
        String updateProductStatusQuery = "update Product set status = 'DELETED' where productIdx=?";
        int updateProductStatusParams = productIdx;

        return this.jdbcTemplate.update(updateProductStatusQuery, updateProductStatusParams);
    }

    public List<GetProductList> searchByTag(String tag) {
        String searchByTag = "select Tag.productIdx, productImgUrl, price, productName\n" +
                "from Product\n" +
                "    left join Tag on Tag.productIdx=Product.productIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where Tag.status='ACTIVE' and ProductImg.status='ACTIVE' and Product.saleStatus = 'ONSALE' and Product.status='ACTIVE' and tag=?";

        String searchByTagParams = tag;


        return this.jdbcTemplate.query(searchByTag,
                (rs, rowNum) -> new GetProductList(
                        // 상품 list (상품 id, 대표사진, 금액, 상품 이름)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName")),
                searchByTagParams
        );
    }


}
