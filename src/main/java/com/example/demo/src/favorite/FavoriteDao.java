package com.example.demo.src.favorite;

import com.example.demo.src.favorite.model.GetFavoriteRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


@Repository
public class FavoriteDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertFavorite(int userIdx, int productIdx) {
        String insertFavoriteQuery = "insert into Favorite(productIdx, userIdx) values(?,?)";
        Object[] insertFavoriteParams = new Object[] {productIdx, userIdx};

        this.jdbcTemplate.update(insertFavoriteQuery, insertFavoriteParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }

    public List<GetFavoriteRes> getFavorites(int userIdx) {
        String getFavoritesQuery = "select favoriteIdx, Favorite.productIdx, productImgUrl, productName, price, saleStatus, User.name, Product.createAt\n" +
                "from Favorite\n" +
                "    left join Product on Favorite.productIdx=Product.productIdx\n" +
                "    left join ProductImg on Favorite.productIdx = ProductImg.productIdx\n" +
                "    left join User on User.userIdx=Product.userIdx\n" +
                "where Favorite.userIdx = ? and favoriteStatus='ACTIVE'\n" +
                "group by favoriteIdx;";
        int getFavoritesParams = userIdx;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        return this.jdbcTemplate.query(getFavoritesQuery,
                (rs, rownum) -> new GetFavoriteRes(
                        // 찜 id, 상품 id, 상품 이미지 1장, 상품 이름,  상품 가격, 상품 판매상태, 판매자 이름, 게시일
                        rs.getInt("favoriteIdx"),
                        rs.getInt("productIdx"),
                        rs.getString("productImgUrl"),
                        rs.getString("productName"),
                        rs.getInt("price"),
                        rs.getString("saleStatus"),
                        rs.getString("name"),
                        sdf.format(rs.getTimestamp("createAt"))),
                        getFavoritesParams);
    }

    public int updateFavoriteStatus(int favoriteIdx) {
        String updateFavoriteStatusQuery = "update Favorite set favoriteStatus='INACTIVE' where favoriteIdx=?";
        int updateFavoriteStatusParams = favoriteIdx;

        return this.jdbcTemplate.update(updateFavoriteStatusQuery, updateFavoriteStatusParams);
    }

    public void reFavorite(int favoriteIdx) {
        String reFavoriteQuery = "update Favorite set favoriteStatus='ACTIVE' where favoriteIdx=?";
        int reFavoriteParams = favoriteIdx;

        this.jdbcTemplate.update(reFavoriteQuery, reFavoriteParams);
    }

    public int getFavoriteIdx(int favoriteIdx, String status) {
        String getFavoriteIdxQuery = "select favoriteIdx from Favorite where favoriteIdx=? and favoriteStatus = ?";
        Object[] getFavoriteIdxParams = new Object[] {favoriteIdx, status};

        try {
            return this.jdbcTemplate.queryForObject(getFavoriteIdxQuery, int.class, getFavoriteIdxParams);
        } catch (Exception exception) {
            return 0;
        }
    }

    public int getFavoriteIdx(int favoriteIdx) { //
        String getFavoriteIdxQuery = "select favoriteIdx from Favorite where favoriteIdx=?";
        int getFavoriteIdxParams = favoriteIdx;

        try {
            return this.jdbcTemplate.queryForObject(getFavoriteIdxQuery, int.class, getFavoriteIdxParams);
        } catch (Exception exception) {
            return 0;
        }
    }

    public int getUserIdxOfFavorite(int favoriteIdx) {
        String getUserIdxOfFavoriteQuery = "select favoriteIdx from Favorite where userIdx=?";
        int getUserIdxOfFavoriteParams = favoriteIdx;

        try {
            return this.jdbcTemplate.queryForObject(getUserIdxOfFavoriteQuery, int.class, getUserIdxOfFavoriteParams);
        } catch (Exception exception) {
            return 0;
        }
    }



}


