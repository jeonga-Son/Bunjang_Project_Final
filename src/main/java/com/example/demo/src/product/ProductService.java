package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.src.product.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public PostProductRes postProducts(int userId, PostProductReq postProductReq) throws BaseException {
        try {
            int productIdx = productDao.insertProducts(userId, postProductReq);
            if(postProductReq.getProductImgs().size() > 0) {
                for (int i = 0; i < postProductReq.getProductImgs().size(); i++) {
                    productDao.insertProductImgs(productIdx, postProductReq.getProductImgs().get(i));
                }
            }
            if(postProductReq.getTag().size() > 0) {
                for (int j = 0; j < postProductReq.getTag().size(); j++) {
                    productDao.insertTags(productIdx, postProductReq.getTag().get(j));
                }
            }
            return new PostProductRes(productIdx);

        } catch (Exception exception) {
            logger.error("App - postProducts Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상품 수정 메서드
    public PatchProductRes patchProduct(int productIdx, PatchProductReq patchProductReq) throws BaseException {
        try {

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
                if (patchProductReq.getTag().size() > 0) {
                    for (int j = 0; j < patchProductReq.getTag().size(); j++) {
                        productDao.insertTags(productIdx, patchProductReq.getTag().get(j));
                    }
                }
            }

            return new PatchProductRes(productIdx);

        } catch (Exception exception) {
            logger.error("App - patchProduct Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 판매상태 변경 메서드
    public PatchProductRes patchSaleStatus(int productIdx, String saleStatus) throws BaseException {
        try {
            int result = productDao.updateSaleStatus(productIdx, saleStatus);

            if (result == 1)
                return new PatchProductRes(productIdx);
            else throw new BaseException(DATABASE_ERROR);
        } catch (Exception exception) {
            logger.error("App - postProducts Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상품 삭제 (상태변경) 메서드
    public PatchProductRes deleteProduct(int productIdx) throws BaseException {
        try {
            int result = productDao.updateProductStatus(productIdx);

            if (result == 1)
                return new PatchProductRes(productIdx);
            else throw new BaseException(DATABASE_ERROR);
        } catch (Exception exception) {
            logger.error("App - deleteProduct Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
