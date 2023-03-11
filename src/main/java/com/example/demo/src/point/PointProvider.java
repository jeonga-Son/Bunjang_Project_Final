package com.example.demo.src.point;

import com.example.demo.config.BaseException;
import com.example.demo.src.point.model.GetPointListRes;
import com.example.demo.src.point.model.GetPointRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class PointProvider {
    private final PointDao pointDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PointProvider(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public List<GetPointRes> getMyPoint(int userIdx) throws BaseException {
        try{
            List<GetPointRes> getPointRes = pointDao.getMyPoint(userIdx);
            return getPointRes;
        } catch (Exception exception) {
            logger.error("App - getMyPoint Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPointListRes> getMyPointList(int userIdx) throws BaseException {
        try{
            List<GetPointListRes> getPointListRes = pointDao.getMyPointList(userIdx);
            return getPointListRes;
        } catch (Exception exception) {
            logger.error("App - getMyPointList Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
