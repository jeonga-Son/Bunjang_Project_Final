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

//    public List<GetReviewList> getReviews(int userIdx){
//        String getReviewsQuery = "select reviewIdx, round(Review.star,1) as star, content, reviewImgUrl, User.name as userName, Review.createAt\n" +
//                "from Review\n" +
//                "    left join User on Review.sellerIdx=User.userIdx\n" +
//                "where Review.sellerIdx=?\n" +
//                "limit 2;";
//
//        int getReviewsParams = userIdx;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//
//        return this.jdbcTemplate.query(getReviewsQuery,
//                (rs, rowNum) -> new GetReviewList (
//                        // 후기 List (2개까지 출력, 리뷰id, 후기 별점, 후기 내용, 후기 사진, 후기 작성자 이름, 작성일자)
//                        rs.getInt("reviewIdx"),
//                        rs.getFloat("star"),
//                        rs.getString("content"),
//                        rs.getString("reviewImgUrl"),
//                        rs.getString("userName"),
//                        sdf.format(rs.getTimestamp("createAt"))),
//                        getReviewsParams);
//    }

    public List<GetReviewList> getReviews(int userIdx, int limit){
        String getReviewsQuery = "select reviewIdx, round(Review.star,1) as star, content, reviewImgUrl, Review.userIdx, Review.createAt, name\n" +
                "from Review left join User on Review.sellerIdx=User.userIdx\n" +
                "where Review.sellerIdx=?\n" +
                "limit ?;";

        Object[] getReviewsParams = new Object[] {userIdx, limit};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        return this.jdbcTemplate.query(getReviewsQuery,
                (rs, rowNum) -> new GetReviewList (
                        // 후기 List (2개까지 출력, 리뷰id, 후기 별점, 후기 내용, 후기 사진, 후기 작성자 이름, 작성일자)
                        rs.getInt("reviewIdx"),
                        rs.getFloat("star"),
                        rs.getString("content"),
                        rs.getString("reviewImgUrl"),
                        rs.getString("name"),
                        sdf.format(rs.getTimestamp("createAt"))),
                getReviewsParams);
    }

    // 회원용 상품 목록 조회
    public List<GetProductList> getProducts_auth(int sellerIdx, int userIdx, int limit) {
        String getProductsQuery = "select prod_list.productIdx, productImgUrl, price, productName, if(isnull(fav_list.productIdx), 0, 1) as isFavorite\n" +
                "from\n" +
                "    (select Product.productIdx,productImgUrl, price, productName\n" +
                "    from User\n" +
                "        left join Product on User.userIdx = Product.userIdx\n" +
                "        left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "        left join Favorite on Product.productIdx=Favorite.productIdx\n" +
                "where User.userIdx=? and Product.status = 'ACTIVE' # 어떤 유저의 상품 목록인지?\n" +
                "group by Product.productIdx\n" +
                "limit ?) prod_list\n" +
                "    left join\n" +
                "        (select productIdx, userIdx\n" +
                "        from Favorite\n" +
                "        where userIdx=? and favoriteStatus = 'ACTIVE' and status = 'ACTIVE') fav_list\n" +
                "    on prod_list.productIdx=fav_list.productIdx";
        Object[] getProductsParams = new Object[] {sellerIdx, limit, userIdx};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름, 찜 여부)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")),
                getProductsParams
                );
    }


    // 비회원용 상품 목록 조회
    public List<GetProductList> getProducts(int userIdx, int limit) {
        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName, (0) as isFavorite\n" +
                "    from User\n" +
                "        left join Product on User.userIdx = Product.userIdx\n" +
                "        left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "        left join Favorite on Product.productIdx=Favorite.productIdx\n" +
                "where User.userIdx=? and Product.status = 'ACTIVE' # 어떤 유저의 상품 목록인지?\n" +
                "group by Product.productIdx\n" +
                "limit ?";
        Object[] getProductsParams = new Object[] {userIdx, limit};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름, 찜 여부)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")),
                getProductsParams
        );
    }

    // 홈화면용 랜덤 상품 출력
    public List<GetProductList> getHomeProducts(int limit) {
        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName, (0) as isFavorite\n" +
                "    from User\n" +
                "        left join Product on User.userIdx = Product.userIdx\n" +
                "        left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "        left join Favorite on Product.productIdx=Favorite.productIdx\n" +
                "where Product.status = 'ACTIVE' and Product.saleStatus = 'ONSALE'\n" +
                "group by Product.productIdx\n" +
                "limit ?";
        Object[] getProductsParams = new Object[] {limit};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름, 찜 여부)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")),
                getProductsParams
        );
    }

    public List<GetProductList> getHomeProducts_auth(int userIdx, int limit) {
        String getProductsQuery = "select prod_list.productIdx, productImgUrl, price, productName, if(isnull(fav_list.productIdx), 0, 1) as isFavorite\n" +
                "from\n" +
                "    (select Product.productIdx,productImgUrl, price, productName" +
                "    from User\n" +
                "        left join Product on User.userIdx = Product.userIdx\n" +
                "        left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "        left join Favorite on Product.productIdx=Favorite.productIdx\n" +
                "where Product.status = 'ACTIVE' and Product.saleStatus = 'ONSALE'\n" +
                "group by Product.productIdx\n" +
                "limit ?\n" +
                ") prod_list\n" +
                "    left join\n" +
                "        (select productIdx, userIdx\n" +
                "        from Favorite\n" +
                "        where userIdx=? and favoriteStatus = 'ACTIVE' and status = 'ACTIVE') fav_list\n" +
                "    on prod_list.productIdx=fav_list.productIdx";
        Object[] getProductsParams = new Object[] {limit, userIdx};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름, 찜 여부)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")),
                getProductsParams
        );
    }

//    public List<GetProductList> getProducts(int userIdx, int limit) {
//        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName\n" +
//                "from User\n" +
//                "    left join Product on User.userIdx = Product.userIdx\n" +
//                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
//                "where User.userIdx=?\n" +
//                "group by Product.productIdx" +
//                "limit ?";
//        Object[] getProductsParams = new Object[] {userIdx, limit};
//
//        return this.jdbcTemplate.query(getProductsQuery,
//                (rs, rowNum) -> new GetProductList(
//                        // 이 상점의 상품 list (상품 id, 대표사진, 금액, 상품 이름)
//                        rs.getInt("productIdx"),
//                        rs.getString("productImgUrl"),
//                        rs.getInt("price"),
//                        rs.getString("productName"),
//                        rs.getInt("isFavorite")
//                        ),
//                getProductsParams
//        );
//    }

//    public List<GetProductList> getProducts() {
//        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName\n" +
//                "from User\n" +
//                "    left join Product on User.userIdx = Product.userIdx\n" +
//                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
//                "where Product.status='ACTIVE' and Product.saleStatus='ONSALE'\n" +
//                "group by Product.productIdx\n" +
//                "limit 6";
//
//        return this.jdbcTemplate.query(getProductsQuery,
//                (rs, rowNum) -> new GetProductList(
//                        // 상품 id, 대표사진, 금액, 상품 이름
//                        rs.getInt("productIdx"),
//                        rs.getString("productImgUrl"),
//                        rs.getInt("price"),
//                        rs.getString("productName"),
//                        rs.getInt("isFavorite"))
//        );
//    }

    public List<GetProductList> getProductsByCat(int categoryIdx) {
        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName, (0) as isFavorite\n" +
                "from User\n" +
                "    left join Product on User.userIdx = Product.userIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where Product.status='ACTIVE' and Product.saleStatus !='SOLD' and categoryIdx=? \n" +
                "group by Product.productIdx\n";
        Object[] getProductsByCatParams = new Object[] {categoryIdx};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 상품 id, 대표사진, 금액, 상품 이름
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")), getProductsByCatParams
        );
    }


    public List<GetProductList> getProductsByCat_auth(int categoryIdx, int userIdx) {
        String getProductsQuery = "select prod_list.productIdx, productImgUrl, price, productName, if(isnull(fav_list.productIdx), 0, 1) as isFavorite\n" +
        "from\n" +
                "    (select Product.productIdx,productImgUrl, price, productName\n" +
                "from User\n" +
                "    left join Product on User.userIdx = Product.userIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where Product.status='ACTIVE' and Product.saleStatus !='SOLD' and categoryIdx=? \n" +
                "group by Product.productIdx\n" +
                ") prod_list\n" +
                "left join\n" +
                "    (select productIdx, userIdx\n" +
                "    from Favorite\n" +
                "    where userIdx=? and favoriteStatus = 'ACTIVE' and status = 'ACTIVE') fav_list\n" +
                "on prod_list.productIdx=fav_list.productIdx";
        Object[] getProductsByCatParams = new Object[] {categoryIdx, userIdx};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 상품 id, 대표사진, 금액, 상품 이름
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")), getProductsByCatParams
        );
    }




    public List<GetProductList> getProductsBySubCat_auth(int subCategoryIdx, int userIdx) {
        String getProductsQuery = "select prod_list.productIdx, productImgUrl, price, productName, if(isnull(fav_list.productIdx), 0, 1) as isFavorite\n" +
                "from\n" +
                "    (select Product.productIdx,productImgUrl, price, productName\n" +
                "from User\n" +
                "    left join Product on User.userIdx = Product.userIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where Product.status='ACTIVE' and Product.saleStatus != 'SOLD' and subCategoryIdx=? \n" +
                "group by Product.productIdx\n" +
                ") prod_list\n" +
                "left join\n" +
                "    (select productIdx, userIdx\n" +
                "    from Favorite\n" +
                "    where userIdx=? and favoriteStatus = 'ACTIVE' and status = 'ACTIVE') fav_list\n" +
                "on prod_list.productIdx=fav_list.productIdx";
        Object[] getProductsBySubCatParams = new Object[] {subCategoryIdx, userIdx};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 상품 id, 대표사진, 금액, 상품 이름
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")), getProductsBySubCatParams
        );
    }

    public List<GetProductList> getProductsBySubCat(int subCategoryIdx) {
        String getProductsQuery = "select Product.productIdx,productImgUrl, price, productName, (0) as isFavorite\n" +
                "from User\n" +
                "    left join Product on User.userIdx = Product.userIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where Product.status='ACTIVE' and Product.saleStatus != 'SOLD' and subCategoryIdx=? \n" +
                "group by Product.productIdx;";

        Object[] getProductsBySubCatParams = new Object[] {subCategoryIdx};

        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetProductList(
                        // 상품 id, 대표사진, 금액, 상품 이름
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")), getProductsBySubCatParams
        );
    }

    public GetShopInfo getShopInfo(int userIdx) {
        String getShopInfo = "select User.userIdx, name, round(avg(Review.star),1) as avgStar, (0) as isFollowing,\n" +
                "        (select count(Follow.followIdx) from User left join Follow on User.userIdx=Follow.followingUserIdx where User.userIdx=?) as followerCount,\n" +
                "        (select count(Product.productIdx) from User left join Product on User.userIdx=Product.userIdx where User.userIdx=? and saleStatus='ONSALE') as productCount,\n" +
                "        (select count(Review.reviewIdx) from User left join Review on User.userIdx=Review.sellerIdx where User.userIdx=?) as reviewCount\n" +
                "from User\n" +
                "        left join Review on User.userIdx=Review.sellerIdx\n" +
                "where User.userIdx=?";
        Object[] getShopInfoParams = new Object[] {userIdx, userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(getShopInfo,
                (rs, rownum) -> new GetShopInfo(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getFloat("avgStar"),
                        rs.getInt("followerCount"),
                        rs.getInt("productCount"),
                        rs.getInt("reviewCount"),
                        rs.getInt("isFollowing")
                ), getShopInfoParams);
    }


    public GetShopInfo getShopInfo_auth(int userIdx, int userIdxByJwt) {
        String getShopInfo = "select shop.userIdx, shop.name, shop.avgStar, shop.followerCount, shop.productCount, shop.reviewCount,\n" +
                "       if(isnull(fol_list.followIdx), 0, 1) as isFollowing\n" +
                "from\n" +
                "    (\n" +
                "select User.userIdx, name, round(avg(Review.star),1) as avgStar,\n" +
                "    (select count(Follow.followIdx) from User left join Follow on User.userIdx=Follow.followingUserIdx where User.userIdx=?) as followerCount,\n" +
                "    (select count(Product.productIdx) from User left join Product on User.userIdx=Product.userIdx where User.userIdx=? and saleStatus='ONSALE') as productCount,\n" +
                "    (select count(Review.reviewIdx) from User left join Review on User.userIdx=Review.sellerIdx where User.userIdx=?) as reviewCount\n" +
                "from User\n" +
                "    left join Review on User.userIdx=Review.sellerIdx\n" +
                "where User.userIdx=?\n" +
                "    ) shop\n" +
                "left join\n" +
                "    (select followIdx, followingUserIdx\n" +
                "    from Follow\n" +
                "    where followerUserIdx=? and followStatus = 'ACTIVE' and status = 'ACTIVE') fol_list\n" +
                "on shop.userIdx=fol_list.followingUserIdx";
        Object[] getShopInfoParams = new Object[] {userIdx, userIdx, userIdx, userIdx, userIdxByJwt};

        return this.jdbcTemplate.queryForObject(getShopInfo,
                (rs, rownum) -> new GetShopInfo(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getFloat("avgStar"),
                        rs.getInt("followerCount"),
                        rs.getInt("productCount"),
                        rs.getInt("reviewCount"),
                        rs.getInt("isFollowing")
                ), getShopInfoParams);
    }

    public GetProductInfoRes getProductInfoRes(int productIdx) {
        String getProductQuery = "select Product.description, Product.productIdx, price, productName, count, productStatus, isExchange, Product.createAt as date, saleStatus,\n" +
                "       SubCategory.subCategoryIdx, SubCategory.subCategoryName, Product.userIdx, (0) as isFavorite,\n" +
                "       (select count(chatRoomIdx)\n" +
                "        from Product left join ChatProduct on Product.productIdx=ChatProduct.productIdx\n" +
                "        where Product.productIdx=?) as chatCount,\n" +
                "       (select count(favoriteIdx)\n" +
                "        from Product left join Favorite on Product.productIdx=Favorite.productIdx\n" +
                "       where Product.productIdx=?) as favoriteCount\n" +
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
                        rs.getInt("isFavorite"), // 쿼리에 추가해야 함
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("count"),
                        rs.getString("productStatus"),
                        rs.getString("isExchange"),
                        rs.getString("description"),
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
                                        "where Product.productIdx = ? and ProductImg.status='ACTIVE';",
                                (rs1, rowNum) -> new PostProductImgs(
                                        rs1.getString("productImgUrl")),
                                rs.getInt("productIdx")),
                        this.jdbcTemplate.query("select tag\n" +
                                "from Tag\n" +
                                "where productIdx = ? and Tag.status='ACTIVE';",
                                (rs2,rowNum1) -> new PostTags(
                                        rs2.getString("tag")
                                ), rs.getInt("productIdx"))
                ), getProductParams);

    }

    public GetProductInfoRes getProductInfoRes_auth(int productIdx, int userIdx) {
        String getProductQuery = "select prod_list.description, prod_list.productIdx, price, productName, prod_list.date as date, saleStatus,\n" +
                "       prod_list.subCategoryIdx, prod_list.subCategoryName, prod_list.userIdx, if(isnull(fav_list.productIdx), 0, 1) as isFavorite,\n" +
                "       prod_list.chatCount, prod_list.favoriteCount\n" +
                "from\n" +
                "    (select Product.description, Product.productIdx, price, productName, count, productStatus, isExchange, Product.createAt as date, saleStatus,\n" +
                "       SubCategory.subCategoryIdx, SubCategory.subCategoryName, Product.userIdx, (0) as isFavorite,\n" +
                "       (select count(chatRoomIdx)\n" +
                "        from Product left join ChatProduct on Product.productIdx=ChatProduct.productIdx\n" +
                "        where Product.productIdx=?) as chatCount,\n" +
                "       (select count(favoriteIdx)\n" +
                "        from Product left join Favorite on Product.productIdx=Favorite.productIdx\n" +
                "       where Product.productIdx=?) as favoriteCount\n" +
                "from Product\n" +
                "    left join User on Product.userIdx=User.userIdx\n" +
                "    left join SubCategory on Product.subCategoryIdx=SubCategory.subCategoryIdx\n" +
                "where Product.productIdx = ?\n" +
                "group by Product.productIdx\n" +
                ") prod_list\n" +
                "    left join\n" +
                "        (select productIdx, userIdx\n" +
                "        from Favorite\n" +
                "        where userIdx=? and favoriteStatus = 'ACTIVE' and status = 'ACTIVE') fav_list\n" +
                "    on prod_list.productIdx=fav_list.productIdx;";
        Object[] getProductParams = new Object[] {productIdx, productIdx, productIdx, userIdx};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        return this.jdbcTemplate.queryForObject(getProductQuery,
                (rs, rownum) -> new GetProductInfoRes(
                        // 상품 id, 금액, 상품 이름, 게시일, 판매상태, 서브 카테고리 id, 서브 카테고리 이름, 작성자 id, 채팅 수, 찜 수
                        // 상품 이미지 불러오기 List
                        // 키워드 불러오기 List
                        rs.getInt("productIdx"),
                        rs.getInt("isFavorite"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("count"),
                        rs.getString("productStatus"),
                        rs.getString("isExchange"),
                        rs.getString("description"),
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
                                        "where Product.productIdx = ? and ProductImg.status='ACTIVE';",
                                (rs1, rowNum) -> new PostProductImgs(
                                        rs1.getString("productImgUrl")),
                                rs.getInt("productIdx")),
                        this.jdbcTemplate.query("select tag\n" +
                                        "from Tag\n" +
                                        "where productIdx = ? and Tag.status='ACTIVE';",
                                (rs2,rowNum1) -> new PostTags(
                                        rs2.getString("tag")
                                ), rs.getInt("productIdx"))
                ), getProductParams);

    }

    public int insertProducts(int userIdx, PostProductReq postProductReq){
        String insertProductsQuery = "insert into Product(categoryIdx, subCategoryidx, userIdx, productName, price, description, count, productStatus, isExchange) values(?,?,?,?,?,?)";
        Object [] insertProductsParams = new Object[] {
                getCategoryIdx(postProductReq.getSubCategoryIdx()),
                postProductReq.getSubCategoryIdx(),
                userIdx,
                postProductReq.getProductName(),
                postProductReq.getPrice(),
                postProductReq.getDescription(),
                postProductReq.getCount(),
                postProductReq.getProductStatus(),
                postProductReq.getIsExchange()
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
        String updateProductQuery = "update Product\n" +
                "set categoryIdx = ?, subCategoryIdx = ?, productName =?, price=?,description=?, count=?, productStatus=?, isExchange=?\n" +
                "where productIdx=?";

        Object[] updateProductParams = new Object[] {
                getCategoryIdx(patchProductReq.getSubCategoryIdx()),
                patchProductReq.getSubCategoryIdx(),
                patchProductReq.getProductName(),
                patchProductReq.getPrice(),
                patchProductReq.getDescription(),
                patchProductReq.getCount(),
                patchProductReq.getProductStatus(),
                patchProductReq.getIsExchange(),
                productIdx};

        return this.jdbcTemplate.update(updateProductQuery, updateProductParams);

    }

    public void deleteProductImgs (int productIdx) {
        String updateProductImgQuery="update ProductImg set status = 'DELETED' where productIdx =?";

        int deleteProductImgsParams = productIdx;
        this.jdbcTemplate.update(updateProductImgQuery, deleteProductImgsParams);

    }

    public void deleteProductTags (int productIdx) {
        String updateProductImgQuery="update Tag set status = 'DELETED' where productIdx =?";

        int deleteProductTagsParams = productIdx;
        this.jdbcTemplate.update(updateProductImgQuery, deleteProductTagsParams);

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

    public List<GetProductList> searchByTag_auth(String tag, int userIdx) {
        String searchByTag = "select prod_list.productIdx, productName, productImgUrl, price, if(isnull(fav_list.productIdx), 0, 1) as isFavorite\n" +
                "from\n" +
                "    (select Tag.productIdx, productImgUrl, price, productName\n" +
                "from Product\n" +
                "    left join Tag on Tag.productIdx=Product.productIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where Tag.status='ACTIVE' and ProductImg.status='ACTIVE' and Product.saleStatus != 'SOLD' and Product.status='ACTIVE' and tag=?\n" +
                ") prod_list\n" +
                "left join\n" +
                "    (select productIdx, userIdx\n" +
                "    from Favorite\n" +
                "    where userIdx=? and favoriteStatus = 'ACTIVE' and status = 'ACTIVE') fav_list\n" +
                "on prod_list.productIdx=fav_list.productIdx\n";
        Object[] searchByTagParams = new Object[] {tag, userIdx};

        return this.jdbcTemplate.query(searchByTag,
                (rs, rowNum) -> new GetProductList(
                        // 상품 list (상품 id, 대표사진, 금액, 상품 이름)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")),
                searchByTagParams
        );

    }

    public List<GetProductList> searchByTag(String tag) {
        String searchByTag = "select Tag.productIdx, productImgUrl, price, productName, (0) as isFavorite\n" +
                "from Product\n" +
                "    left join Tag on Tag.productIdx=Product.productIdx\n" +
                "    left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                "where Tag.status='ACTIVE' and ProductImg.status='ACTIVE' and Product.saleStatus != 'SOLD' and Product.status='ACTIVE' and tag=?\n";
        Object[] searchByTagParams = new Object[] {tag};

        return this.jdbcTemplate.query(searchByTag,
                (rs, rowNum) -> new GetProductList(
                        // 상품 list (상품 id, 대표사진, 금액, 상품 이름)
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("isFavorite")),
                searchByTagParams
        );

    }

    public int getCategoryIdx (int subCategoryIdx) {
        String getCategoryIdxQuery = "select categoryIdx from SubCategory where subCategoryIdx=? and status='ACTIVE'";

        int getCategoryIdxParams = subCategoryIdx;

        return this.jdbcTemplate.queryForObject(getCategoryIdxQuery, int.class, getCategoryIdxParams);

    }


    // 존재하는 상품인지?
    public int checkProductExists(int productIdx){
        String checkProductExistsQuery = "select exists(select productIdx from Product where productIdx = ? and status='ACTIVE');";
        int checkProductExistsParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkProductExistsQuery, int.class, checkProductExistsParams);
    }

    // 존재하는 서브 카테고리인지?
    public int checkSubCategoryExists(int subCategoryIdx){
        String checkSubCategoryExistsQuery = "select exists(select subCategoryIdx from SubCategory where subCategoryIdx = ? and status='ACTIVE');";
        int checkSubCategoryExistsParams = subCategoryIdx;
        return this.jdbcTemplate.queryForObject(checkSubCategoryExistsQuery, int.class, checkSubCategoryExistsParams);
    }

    // 판매상태 반환 함수
    public String getSaleStatus(int productIdx){
        String checkSaleStatusQuery = "select saleStatus from Product where productIdx=?";
        int checkSaleStatusParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkSaleStatusQuery, String.class, checkSaleStatusParams);
    }

    // 해당 상품을 등록한 유저 id 반환 함수
    public int getUserIdxOfProduct(int productIdx) {
        String getUserIdxOfProductQuery = "select userIdx from Product where productIdx=?";
        int getUserIdxOfProductParams = productIdx;
        return this.jdbcTemplate.queryForObject(getUserIdxOfProductQuery, int.class, getUserIdxOfProductParams);
    }



}
