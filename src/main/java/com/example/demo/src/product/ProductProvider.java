package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
@Service
public class ProductProvider {

    private final ProductDao productDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }

    // 리뷰 목록 불러오기
    public List<GetReviewList> getReviews(int productIdx) throws BaseException {
        try {
            int userIdx = productDao.getUserIdxByProductIdx(productIdx);
            List<GetReviewList> getReviewList = productDao.getReviews(userIdx);
            return getReviewList;
        } catch (Exception exception) {
            logger.error("App - getReviews Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이 상점의 상품 목록 불러오기
    public List<GetProductList> getProducts(int productIdx) throws BaseException {
        try {
            int userIdx = productDao.getUserIdxByProductIdx(productIdx);
            List<GetProductList> getProductList = productDao.getProducts(userIdx);
            return getProductList;
        } catch (Exception exception) {
            logger.error("App - getProducts Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 상점 정보 불러오기
    public GetShopInfo getShopInfo(int userIdx) throws BaseException {
        try {
            GetShopInfo getShopInfo = productDao.getShopInfo(userIdx);
            return getShopInfo;
        } catch (Exception exception) {
            logger.error("App - getShopInfo Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상점 정보 불러오기
    public GetProductInfoRes getProductInfoRes(int productIdx) throws BaseException {
        try {
            GetProductInfoRes getProductInfoRes = productDao.getProductInfoRes(productIdx);
            return getProductInfoRes;
        } catch (Exception exception) {
            logger.error("App - getProductRes Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }








}
