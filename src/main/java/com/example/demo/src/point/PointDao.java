package com.example.demo.src.point;

import com.example.demo.src.point.model.GetPointListRes;
import com.example.demo.src.point.model.GetPointRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PointDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<GetPointRes> getMyPoint(int userIdx) {
        // 포인트 조회
        String getMyPointQuery = "select SUM(Point.point) as totalPoint, (select DATEDIFF(date(Point.expireDate), now())\n" +
                "    from Point\n" +
                "        left join User on User.userIdx = Point.userIdx\n" +
                "        where DATEDIFF(date(Point.expireDate), now()) < 30 and\n" +
                "            DATEDIFF(date(Point.expireDate), now()) > 0 and\n" +
                "            User.userIdx = ? and Point.pointName = '포인트적립'\n" +
                "           or Point.pointName LIKE '%리워드%' and Point.point > 0) as countExpireDate\n" +
                "from Point\n" +
                "    right join User on User.userIdx = Point.userIdx\n" +
                "    where User.userIdx = ? ;";

        Object[] getMyPointParams = new Object[]{userIdx, userIdx};

        return this.jdbcTemplate.query(getMyPointQuery,
                (rs, rowNum) -> new GetPointRes(
                        rs.getInt("totalPoint"),
                        rs.getLong("countExpireDate")
                ), getMyPointParams);
    }

    // 포인트 사용 내역 조회
    public List<GetPointListRes> getMyPointList(int userIdx) {
        String getMyPointListQuery = "select Point.point, Point.expireDate,  Point.pointName, Point.createAt\n" +
                "from Point\n" +
                "    right join User on User.userIdx = Point.userIdx\n" +
                "    where User.userIdx = ?;";
        int getMyPointParam = userIdx;

        return this.jdbcTemplate.query(getMyPointListQuery,
                (rs, rowNum) -> new GetPointListRes(
                        rs.getInt("point"),
                        rs.getString("pointName"),
                        rs.getDate("expireDate"),
                        rs.getTimestamp("createAt")
                ), getMyPointParam);
    }
}
