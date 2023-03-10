package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.GetProductInfoRes;
import com.example.demo.src.product.model.GetProductList;
import com.example.demo.src.product.model.GetReviewList;
import com.example.demo.src.product.model.GetShopInfo;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.PRODUCT_NOT_EXISTS;
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
        // validation : 존재하는 상품인지?
        if(checkProductExists(productIdx) == 0)
            throw new BaseException(PRODUCT_NOT_EXISTS);
        try {
            int userIdx = productDao.getUserIdxByProductIdx(productIdx);
            List<GetProductList> getProductList = productDao.getProducts(userIdx);
            return getProductList;
        } catch (Exception exception) {
            logger.error("App - getProducts Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 랜덤으로 상품 목록 뽑아내기 (홈 화면 용)
    public List<GetProductList> getProducts() throws BaseException {
        try {
            List<GetProductList> getProductList = productDao.getProducts();
            return getProductList;
        } catch (Exception exception) {
            logger.error("App - getProducts Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 카테고리 별 상품 목록 뽑아내기
    public List<GetProductList> getProductsByCat(int categoryIdx) throws BaseException {
        try {
            List<GetProductList> getProductList = productDao.getProductsByCat(categoryIdx);
            return getProductList;
        } catch (Exception exception) {
            logger.error("App - getProducts Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetProductList> getProductsBySubCat(int subCategoryIdx) throws BaseException {
        try {
            List<GetProductList> getProductList = productDao.getProductsBySubCat(subCategoryIdx);
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

    // 상품 정보 불러오기
    public GetProductInfoRes getProductInfoRes(int productIdx) throws BaseException {
        try {
            GetProductInfoRes getProductInfoRes = productDao.getProductInfoRes(productIdx);
            return getProductInfoRes;
        } catch (Exception exception) {
            logger.error("App - getProductRes Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 태그 검색 메서드
    public List<GetProductList> getProductsByTag(String tag) throws BaseException {
        try {
            List<GetProductList> getProductLists = productDao.searchByTag(tag);
            return getProductLists;
        } catch (Exception exception) {
            logger.error("App - getProductsByTag Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 존재하는 상품인지?
    public int checkProductExists(int productIdx) throws BaseException{
        try{
            return productDao.checkProductExists(productIdx);
        } catch (Exception exception){
            logger.error("App - checkProductExists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 존재하는 서브 카테고리인지?
    public int checkSubCategoryExists(int subCategoryIdx) throws BaseException{
        try{
            return productDao.checkSubCategoryExists(subCategoryIdx);
        } catch (Exception exception){
            logger.error("App - checkSubCategoryExists Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }









}
