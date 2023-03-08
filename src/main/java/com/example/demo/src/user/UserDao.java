package com.example.demo.src.user;

import com.example.demo.src.product.model.GetProductList;
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
        String getUserQuery = "select User.userIdx, User.name, phoneNo, birthday,\n" +
                "       address, latitude, longitude, User.status, profileImgURl,\n" +
                "       shopDescription,\n" +
                "       (select count(Follow.followingUserIdx)\n" +
                "from User\n" +
                "    left join Follow on User.userIdx = Follow.followingUserIdx\n" +
                " where userIdx = ?) AS followerCount,\n" +
                "     (select count(Follow.followerUserIdx)\n" +
                "from User\n" +
                "    left join Follow on User.userIdx = Follow.followerUserIdx\n" +
                " where userIdx = ?) AS followingCount from User where userIdx = ?";

        Object[] getUserParams = new Object[]{userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("phoneNo"),
                        rs.getDate("birthday"),
                        rs.getString("address"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getString("status"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription")
                ), getUserParams);
    }

    public GetMyPageRes getMyPage(int userIdx){
        // 마이페이지 조회
        String getUserQuery = "Select u.userIdx, u.profileImgUrl ,u.name, p.point, AVG(r.star) as avgStar,\n" +
                "       (select count(followerUserIdx) from Follow where Follow.followingUserIdx=? and status='ACTIVE') AS followingCount,\n" +
                "       (select count(followingUserIdx) from Follow where Follow.followerUserIdx=? and status = 'ACTIVE') AS followerCount,\n" +
                "       (select count(productIdx) from Product where userIdx=? and status = 'ACTIVE') As TotalProduct,\n" +
                "       pd.productName, sum(pd.price) pointBalance, pd.saleStatus\n" +
                "From User u\n" +
                "    left join Point p on u.userIdx=p.userIdx\n" +
                "    left join Product pd on u.userIdx=pd.userIdx\n" +
                "    left join Review r on u.userIdx = r.sellerIdx\n" +
                "where u.userIdx = ?";

        GetUserRes getUser = getUser(userIdx);
        Object[] getUserParams = new Object[]{userIdx, userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetMyPageRes(
                        // 유저 id, 이름, 프로필이미지Url, 상점 설명, 포인트 잔액, 팔로워 id, 팔로잉 id
                        // 상품 id, 상품이름, 상품 가격,상품판매 상태, 상품 이미지 불러오기 List
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getFloat("avgStar"),
                        rs.getInt("point"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        this.jdbcTemplate.query("select Product.productIdx, Product.productName, Product.price, ProductImg.productImgUrl\n" +
                                        "from Product\n" +
                                        "left join ProductImg on Product.productIdx=ProductImg.productIdx\n" +
                                        "left join User on Product.userIdx=User.userIdx\n" +
                                        "where User.userIdx = ? and ProductImg.status='ACTIVE';",
                                (rs2, rowNum2) -> new GetProductList(
                                        rs2.getInt("productIdx"),
                                        rs2.getString("productImgUrl"),
                                        rs2.getInt("price"),
                                        rs2.getString("productName")),
                                rs.getInt("userIdx"))
                ), getUserParams);
    }

    public GetShopRes getShop(int userIdx) {
        // 상점 조회
        String getUserQuery = "select User.userIdx,User.name, AVG(Review.star) as avgStar, User.profileImgUrl, User.shopDescription,\n" +
                "       (select count(followerUserIdx) from Follow where Follow.followingUserIdx=? and status='ACTIVE') AS followerCount,\n" +
                "       (select count(followingUserIdx) from Follow where Follow.followerUserIdx=? and status='ACTIVE') AS followingCount,\n" +
                "       (select count(productIdx) from Product where userIdx=? and status = 'ACTIVE') As TotalProduct\n" +
                "from User\n" +
                "    left join Review on User.userIdx = Review.userIdx\n" +
                "    left join Follow on Review.status = Follow.status\n" +
                "    left join Product on Review.productIdx = Product.productIdx\n" +
                "    left join ProductImg on Product.productIdx = ProductImg.productIdx\n" +
                "    where User.userIdx=? and ProductImg.status='ACTIVE';";
        Object[] getUserParams = new Object[]{userIdx, userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetShopRes(
                        // 상호명, 후기 평점, 팔로워, 팔로잉, 판매상품 개수, 판매상품 list(최신순)
                        // 상품 id, 상품이름, 상품 가격,상품판매 상태, 상품 이미지 불러오기 List
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        this.jdbcTemplate.query("select Product.productIdx, Product.productName, Product.price, ProductImg.productImgUrl\n" +
                                        "from Product\n" +
                                        "    left join User on User.userIdx = Product.userIdx\n" +
                                        "    left join ProductImg on Product.productIdx = ProductImg.productIdx\n" +
                                        "    where User.userIdx = ? and Product.status='ACTIVE' and ProductImg.status='ACTIVE';",
                                (rs2, rowNum2) -> new GetProductList(
                                        rs2.getInt("productIdx"),
                                        rs2.getString("productImgUrl"),
                                        rs2.getInt("price"),
                                        rs2.getString("productName")),
                                rs.getInt("userIdx"))
                ), getUserParams);
    }

    public int checkUser(PostUserReq postUserReq) {
        String checkUserQuery = "select exists(select name, phoneNo, birthday from User where phoneNo = ?)";
        String checkUserParam = postUserReq.getPhoneNo();
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                int.class,
                checkUserParam);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, phoneNo, birthday) VALUES (?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getPhoneNo(), postUserReq.getBirthday()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkPhoneNo(PostUserReq postUserReq) {
        String checkUserIdxQuery = "select exists(select name, phoneNo from User where phoneNo = ?)";
        String checkPhoneNoParma = postUserReq.getPhoneNo();

        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,int.class, checkPhoneNoParma);
    }

    public int deleteUser(int userIdx, PatchDeleteUserReq patchDeleteUserReq) {
        String deleteUserQuery = "update User set status = 'Deleted' where userIdx = ? ";
        String insertDeleteCommentQuery = "insert into DeleteReason (userIdx, content) values(?, ?)";
        Object[] insertDeleteUserParams = new Object[]{patchDeleteUserReq.getUserIdx(), patchDeleteUserReq.getContent()};
        this.jdbcTemplate.update(insertDeleteCommentQuery, insertDeleteUserParams);
        return this.jdbcTemplate.update(deleteUserQuery,userIdx);
    }

    public int modifyShop(int userIdx, PatchShopInfoReq patchShopInfoReq) {
        String modifyShopQuery = "update User set profileImgUrl = ?, shopDescription = ?  where userIdx = ? ";
        Object[] modifyShopParams = new Object[]{patchShopInfoReq.getProfileImgUrl(), patchShopInfoReq.getShopDescription(), userIdx};
        return this.jdbcTemplate.update(modifyShopQuery, modifyShopParams);
    }

}
