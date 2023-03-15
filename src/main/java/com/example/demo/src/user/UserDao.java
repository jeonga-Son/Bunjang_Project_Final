package com.example.demo.src.user;

import com.example.demo.src.product.model.GetProductList;
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
        String getUserQuery = "select userIdx, name, phoneNo, birthday, address, latitude,\n" +
                "       longitude, createAt, updateAt, status, profileImgUrl,\n" +
                "       shopDescription, deleteReasonContent,\n" +
                "       (select count(Follow.followingUserIdx)\n" +
                "        from User\n" +
                "            left join Follow on User.userIdx = Follow.followingUserIdx\n" +
                "             where userIdx = ?) AS followerCount,\n" +
                "        (select count(Follow.followerUserIdx)\n" +
                "        from User\n" +
                "            left join Follow on User.userIdx = Follow.followerUserIdx\n" +
                "            where userIdx = ?) AS followingCount\n" +
                "from User where userIdx = ?;";

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
                        rs.getString("shopDescription"),
                        rs.getString("deleteReasonContent")
                ), getUserParams);
    }

    public GetMyPageRes getMyPage(int userIdx){
        // 마이페이지 조회
        String getUserQuery = "Select User.userIdx, User.profileImgUrl ,User.name, sum(Point.point) as point, AVG(Review.star) as avgStar,\n" +
                "                       (select count(followerUserIdx) from Follow where Follow.followingUserIdx=? and status='ACTIVE') AS followingCount,\n" +
                "                       (select count(followingUserIdx) from Follow where Follow.followerUserIdx=? and status = 'ACTIVE') AS followerCount,\n" +
                "                       (select count(Product.productIdx) from Product where Product.userIdx = ? and Product.status = 'ACTIVE' and Product.saleStatus != 'SOLD') As TotalProductCount,\n" +
                "                       (select count(Product.saleStatus) from Product where Product.userIdx = ? and Product.saleStatus = 'SOLD') AS saleResultCount\n" +
                "                From User\n" +
                "                    left join Point on User.userIdx = Point.userIdx\n" +
                "                    left join Review on User.userIdx = Review.userIdx\n" +
                "                    where User.userIdx = ?;";

        Object[] getUserParams = new Object[]{userIdx, userIdx, userIdx, userIdx, userIdx};

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
                        this.jdbcTemplate.query("select Product.productIdx, Product.price, Product.productName, ProductImg.productImgUrl , 0 as isFavorite\n" +
                                        "    from Product\n" +
                                        "            left join ProductImg on Product.productIdx = ProductImg.productIdx\n" +
                                        "            where Product.userIdx = ? and ProductImg.status='ACTIVE' and Product.status='ACTIVE' and Product.saleStatus = 'ONSALE'\n" +
                                        "    Group by Product.productIdx",
                                (rs2, rowNum2) -> new GetProductList(
                                        rs2.getInt("productIdx"),
                                        rs2.getString("productImgUrl"),
                                        rs2.getInt("price"),
                                        rs2.getString("productName"),
                                        rs2.getInt("isFavorite")),
                                rs.getInt("userIdx"))
                ), getUserParams);
    }

    public GetStoreRes getStore(int userIdx) {
        // 상점 조회
        String getUserQuery = "select User.userIdx,User.name, AVG(Review.star) as avgStar, User.profileImgUrl, User.shopDescription,\n" +
                "                       (select count(followerUserIdx) from Follow where Follow.followingUserIdx=? and status='ACTIVE') AS followerCount,\n" +
                "                       (select count(followingUserIdx) from Follow where Follow.followerUserIdx=? and status='ACTIVE') AS followingCount\n" +
                "                from User\n" +
                "                    left join Review on User.userIdx = Review.userIdx\n" +
                "                    left join Follow on Review.status = Follow.status\n" +
                "                    left join Product on Review.productIdx = Product.productIdx\n" +
                "                    left join ProductImg on Product.productIdx = ProductImg.productIdx\n" +
                "                    where User.userIdx=? and ProductImg.status='ACTIVE'";
        Object[] getUserParams = new Object[]{userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount")
                ), getUserParams);
    }

    // 상점 상품 조회
    public List<GetStoreProductsRes> getStoreProducts(int userIdx) {
        String getUserQuery = "select Product.productIdx, (select count(productIdx) from Product where userIdx=? and status = 'ACTIVE') As TotalProduct,\n" +
                "       Product.productName, Product.price, ProductImg.productImgUrl\n" +
                "                                        from Product\n" +
                "                                            left join User on User.userIdx = Product.userIdx\n" +
                "                                            left join ProductImg on Product.productIdx = ProductImg.productIdx\n" +
                "                                            where User.userIdx = ? and Product.status='ACTIVE' and ProductImg.status='ACTIVE'\n" +
                "                                            order by Product.createAt desc;";
        Object[] getUserParams = new Object[]{userIdx, userIdx};

        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreProductsRes(
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getInt("price"),
                        rs.getString("productName")),
                getUserParams);

    }

    public int checkKakaoUserName(String kakaoUserName) {
        String checkUserIdxQuery = "select exists(select name from User where name = ?)";
        String checkUserParam = kakaoUserName;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery, int.class,checkUserParam);
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

    public int deleteUser(PatchDeleteUserReq patchDeleteUserReq) {
        String deleteUserQuery = "update User set status = 'DELETED', deleteReasonContent = ?, updateAt = ? where userIdx = ? ";
        Object[] insertDeleteReasonParams = new Object[]{ patchDeleteUserReq.getDeleteReasonContent(),patchDeleteUserReq.getUpdateAt(), patchDeleteUserReq.getUserIdx()};

        return this.jdbcTemplate.update(deleteUserQuery,insertDeleteReasonParams);
    }

    public int modifyShop(PatchShopInfoReq patchShopInfoReq) {
        String modifyShopQuery = "update User set profileImgUrl = ?, shopDescription = ?, name = ? where userIdx = ? ";
        Object[] modifyShopParams = new Object[]{patchShopInfoReq.getProfileImgUrl(), patchShopInfoReq.getShopDescription(), patchShopInfoReq.getName() , patchShopInfoReq.getUserIdx()};
        return this.jdbcTemplate.update(modifyShopQuery, modifyShopParams);
    }

    public User checkUser(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userIdx, name, phoneNo, birthday, address, latitude, longitude,\n" +
                "       createAt, updateAt, status, profileImgUrl, shopDescription, deleteReasonContent\n" +
                "From User\n" +
                "    where phoneNo = ? and name = ?;";
        Object[] checkUserParams = new Object[]{postLoginReq.getPhoneNo(), postLoginReq.getName()};

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("phoneNo"),
                        rs.getDate("birthday"),
                        rs.getString("address"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getTimestamp("createAt"),
                        rs.getTimestamp("updateAt"),
                        rs.getString("status"),
                        rs.getString("profileImgUrl"),
                        rs.getString("shopDescription"),
                        rs.getString("deleteReasonContent")

                ),
                checkUserParams
        );
    }

    public int checkPhoneNo(String phoneNo) {
        String checkPhoneNoQuery = "select exists(select phoneNo from User where phoneNo = ?)";
        String checkPhoneNoParams = phoneNo;
        return this.jdbcTemplate.queryForObject(checkPhoneNoQuery,
                int.class,
                checkPhoneNoParams);
    }

    // userIdx가
    public int checkUserIdx(int userIdx) {
        String checkUserIdxQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserIdxParam = userIdx;

        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParam);
    }
}
