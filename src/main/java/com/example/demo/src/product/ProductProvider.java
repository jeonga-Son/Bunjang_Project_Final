package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.RichException;
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
        // int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
        try {
            int userIdx = productDao.getUserIdxByProductIdx(productIdx);
            List<GetReviewList> getReviewList = productDao.getReviews(userIdx, 2);
            return getReviewList;
        } catch (Exception exception) {
            logger.error("App - getReviews Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이 상점의 상품 목록 불러오기
    public List<GetProductList> getProducts(int productIdx) throws BaseException {
        // validation : 존재하는 상품인지?
        if (checkProductExists(productIdx) == 0)
            throw new BaseException(PRODUCT_NOT_EXISTS);

        try { // 회원이 접속했을 때
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            try {
                int sellerIdx = productDao.getUserIdxByProductIdx(productIdx);
                List<GetProductList> getProductList = productDao.getProducts_auth(sellerIdx, userIdxByJwt, 3); // jwt에서 추출한 userIdx 넣어줘야함
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);

            }
        } catch (BaseException e) { // 비회원
            try { // 비회원
                int sellerIdx = productDao.getUserIdxByProductIdx(productIdx);
                List<GetProductList> getProductList = productDao.getProducts(sellerIdx, 3);
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }



    // 랜덤으로 상품 목록 뽑아내기 (홈 화면 용)
    public List<GetProductList> getHomeProducts() throws BaseException {
        try { // 회원이 접속했을 때
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            try {
                List<GetProductList> getProductList = productDao.getHomeProducts_auth(userIdxByJwt, 500);
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        } catch (Exception e) { // 비회원이 접속했을 때
            try {
                List<GetProductList> getProductList = productDao.getHomeProducts(500);
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }

        }
    }

    // 카테고리 별 상품 목록 뽑아내기
    public List<GetProductList> getProductsByCat(int categoryIdx) throws BaseException {
        try { // 회원이 접속했을 때
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            try {
                List<GetProductList> getProductList = productDao.getProductsByCat_auth(categoryIdx, userIdxByJwt);
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        } catch (Exception e) { // 비회원
            try {
                List<GetProductList> getProductList = productDao.getProductsByCat(categoryIdx);
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        }

    }


    // 서브 카테고리 상품 목록 뽑아내기
    public List<GetProductList> getProductsBySubCat(int subCategoryIdx) throws BaseException {
        try { // 회원용
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            try {
                List<GetProductList> getProductList = productDao.getProductsBySubCat_auth(subCategoryIdx, userIdxByJwt);
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        } catch (Exception e) {
            try { // 비회원용
                List<GetProductList> getProductList = productDao.getProductsBySubCat(subCategoryIdx);
                return getProductList;
            } catch (Exception exception) {
                logger.error("App - getProducts Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }



    // 상점 정보 불러오기
    public GetShopInfo getShopInfo(int productIdx) throws BaseException {

        try {
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            try {
                int userIdx = productDao.getUserIdxByProductIdx(productIdx);
                GetShopInfo getShopInfo = productDao.getShopInfo_auth(userIdx, userIdxByJwt);
                return getShopInfo;
            } catch (Exception exception) {
                logger.error("App - getShopInfo Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        } catch (Exception e) {
            try {
                int userIdx = productDao.getUserIdxByProductIdx(productIdx);
                GetShopInfo getShopInfo = productDao.getShopInfo(userIdx);
                return getShopInfo;
            } catch (Exception exception) {
                logger.error("App - getShopInfo Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    // 상품 정보 불러오기
    public GetProductInfoRes getProductInfoRes(int productIdx) throws BaseException, RichException {
        // validation : 존재하는 상품인지?
        if (productDao.checkProductExists(productIdx) == 0)
            throw new BaseException(PRODUCT_NOT_EXISTS);
        try {
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            try {
                GetProductInfoRes getProductInfoRes = productDao.getProductInfoRes_auth(productIdx, userIdxByJwt);
                return getProductInfoRes;
            } catch (Exception exception) {
                logger.error("App - getProductRes Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        } catch (BaseException be) {
            try {
                GetProductInfoRes getProductInfoRes = productDao.getProductInfoRes(productIdx);
                return getProductInfoRes;
            } catch (Exception exception) {
                logger.error("App - getProductRes Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    // 태그 검색 메서드
    public List<GetProductList> getProductsByTag(String tag) throws BaseException {
        try {
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            try {
                List<GetProductList> getProductLists = productDao.searchByTag_auth(tag, userIdxByJwt);
                return getProductLists;
            } catch (Exception exception) {
                logger.error("App - getProductsByTag Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
        } catch (Exception e) {
            try {
                List<GetProductList> getProductLists = productDao.searchByTag(tag);
                return getProductLists;
            } catch (Exception exception) {
                logger.error("App - getProductsByTag Provider Error", exception);
                throw new BaseException(DATABASE_ERROR);
            }
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
