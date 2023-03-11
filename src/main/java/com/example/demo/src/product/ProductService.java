package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.config.RichException;
import com.example.demo.src.product.model.PatchProductReq;
import com.example.demo.src.product.model.PatchProductRes;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
    private final ProductProvider productProvider;
    private final JwtService jwtService;


    @Autowired
    public ProductService(ProductDao productDao, ProductProvider productProvider, JwtService jwtService) {
        this.productDao = productDao;
        this.productProvider = productProvider;
        this.jwtService = jwtService;

    }

    // 상품 등록 메서드 (상품 정보 + 이미지 + 키워드)
    public PostProductRes postProducts(int userId, PostProductReq postProductReq) throws BaseException, RichException {
        try {
            // validation : 존재하는 서브 카테고리인지?
            if(checkSubCategoryExists(postProductReq.getSubCategoryIdx()) == 0)
                throw new RichException(SUBCATEGORY_NOT_EXISTS);


            int productIdx = productDao.insertProducts(userId, postProductReq);

            if(postProductReq.getProductImgs().size() > 0) {
                for (int i = 0; i < postProductReq.getProductImgs().size(); i++) {
                    productDao.insertProductImgs(productIdx, postProductReq.getProductImgs().get(i));
                }
            }
            if(postProductReq.getTags().size() > 0) {
                for (int j = 0; j < postProductReq.getTags().size(); j++) {
                    productDao.insertTags(productIdx, postProductReq.getTags().get(j));
                }
            }
            return new PostProductRes(productIdx);
        } catch (RichException richException) {
            logger.error("App - postProducts Service Error", richException);
            throw new RichException(richException.getStatus());
        }
        catch (Exception exception) {
            logger.error("App - postProducts Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }


    }

    // 상품 수정 메서드
    public PatchProductRes patchProduct(int productIdx, PatchProductReq patchProductReq) throws BaseException, RichException {
        try {

            // validation : 존재하는 상품인지?
            if(productDao.checkProductExists(productIdx) == 0)
                throw new RichException(PRODUCT_NOT_EXISTS);

            // 회원용 API : 권한이 있는 유저인가? (이 상품의 작성자인가?)
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (getUserIdxOfProduct(productIdx) != userIdxByJwt)
                throw new RichException(INVALID_USER_JWT);

            // validation : 존재하는 서브 카테고리인지?
            if(checkSubCategoryExists(patchProductReq.getSubCategoryIdx()) == 0)
                throw new RichException(SUBCATEGORY_NOT_EXISTS);


            // 상품 수정
            int result = productDao.updateProduct(productIdx, patchProductReq);

            // 새로운 이미지, 태그 추가
            if (result == 1) { // 상품 수정에 성공했을 경우
                // 기존 이미지, 태그 삭제
                productDao.deleteProductImgs(productIdx);
                productDao.deleteProductTags(productIdx);

                // 새로운 이미지, 태그 추가
                if (patchProductReq.getProductImgs().size() > 0) {
                    for (int i = 0; i < patchProductReq.getProductImgs().size(); i++) {
                        productDao.insertProductImgs(productIdx, patchProductReq.getProductImgs().get(i));
                    }
                }
                if (patchProductReq.getTags().size() > 0) {
                    for (int j = 0; j < patchProductReq.getTags().size(); j++) {
                        productDao.insertTags(productIdx, patchProductReq.getTags().get(j));
                    }
                }
                return new PatchProductRes(productIdx);

            } else throw new BaseException(DATABASE_ERROR);


        } catch (RichException richException) {
            logger.error("App - patchProduct Service Error", richException);
            throw new RichException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - patchProduct Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 판매상태 변경 메서드
    public PatchProductRes patchSaleStatus(int productIdx, String saleStatus) throws BaseException, RichException {
        try {
            // validation : 존재하는 상품인지?
            if(checkProductExists(productIdx) == 0)
                throw new RichException(PRODUCT_NOT_EXISTS);

            // 회원용 API : 권한이 있는 유저인가? (이 상품의 작성자인가?)
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (getUserIdxOfProduct(productIdx) != userIdxByJwt)
                throw new RichException(INVALID_USER_JWT);

            // validation : 요청값의 saleStatus값이 올바른지?
            List<String> saleStatusList = Arrays.asList("ONSALE", "ORDERED", "SOLD");
            if(!saleStatusList.contains(saleStatus))
                throw new RichException(PATCH_INVALID_PRODUCT_STATUS);

            // validation : 요청하는 판매 상태와 기존 판매상태가 같은지?
            if(saleStatus == getSaleStatus(productIdx))
                return new PatchProductRes(productIdx); // 같은 경우 데이터 수정을 거치지 않고 그냥 반환한다.

            int result = productDao.updateSaleStatus(productIdx, saleStatus);

            if (result == 1)
                return new PatchProductRes(productIdx);
            else throw new BaseException(DATABASE_ERROR);
        } catch (RichException richException) {
            logger.error("App - postProducts Service Error", richException);
            throw new RichException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - postProducts Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상품 삭제 메서드
    public PatchProductRes deleteProduct(int productIdx) throws BaseException, RichException {
        try {

            // 회원용 API : 권한이 있는 유저인가? (이 상품의 작성자인가?)
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (getUserIdxOfProduct(productIdx) != userIdxByJwt)
                throw new RichException(INVALID_USER_JWT);

            // validation : 존재하는 상품인지?
            if(productDao.checkProductExists(productIdx) == 0)
                throw new RichException(PRODUCT_NOT_EXISTS);

            // validation : 판매 상태가 예약중, 판매완료 인지?
            if(getSaleStatus(productIdx) == "ONSALE")
                throw new RichException(INVALID_PRODUCT_STATUS);


            int result = productDao.updateProductStatus(productIdx);

            if (result == 1)
                return new PatchProductRes(productIdx);
            else throw new BaseException(DATABASE_ERROR);
        } catch (RichException richException) {
            logger.error("App - postProducts Service Error", richException);
            throw new RichException(richException.getStatus());
        } catch (Exception exception) {
            logger.error("App - deleteProduct Service Error", exception);
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

    // 판매 상태 반환 메서드
    public String getSaleStatus(int productIdx) throws BaseException{
        try{
            return productDao.getSaleStatus(productIdx);
        } catch (Exception exception){
            logger.error("App - getSaleStatus Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getUserIdxOfProduct(int productIdx) throws BaseException {
        try{
            return productDao.getUserIdxOfProduct(productIdx);
        } catch (Exception exception){
            logger.error("App - getUserIdxOfProduct Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }






}
