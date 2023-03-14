package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/products")
public class ProductController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;

    public ProductController(ProductProvider productProvider, ProductService productService, JwtService jwtService) {
        this.productProvider = productProvider;
        this.productService = productService;
        this.jwtService = jwtService;
    }

    /**
     * 특정 상품 조회 API
     *
     * @param productIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{productIdx}") // (GET) 127.0.0.1:9000/products/:productIdx
    public BaseResponse<GetProductRes> getProductRes(@PathVariable("productIdx") int productIdx) {
        try {
            List<GetProductList> getProductList = productProvider.getProducts(productIdx);
            List<GetReviewList> getReviewList = productProvider.getReviews(productIdx);
            GetShopInfo getShopInfo = productProvider.getShopInfo(productIdx);
            GetShopRes getShopRes = new GetShopRes(getReviewList, getProductList, getShopInfo);

            GetProductInfoRes getProductInfoRes = productProvider.getProductInfoRes(productIdx);
            GetProductRes getProductRes = new GetProductRes(getProductInfoRes, getShopRes);
            return new BaseResponse<>(getProductRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 상품 등록 api
     *
     * @param postProductReq
     * @return
     */
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/products
    public BaseResponse<PostProductRes> postProductRes( @RequestBody PostProductReq postProductReq) {
//        // validation : 상품명을 입력했는지?
//        if(postProductReq.getProductName().isEmpty())
//            return new BaseResponse<>(EMPTY_PRODUCT_NAME);
//
//        // validation : 내용을 입력했는지?
//        if(postProductReq.getDescription().isEmpty())
//            return new BaseResponse<>(EMPTY_PRODUCT_NAME);
//        // validation : 이미지를 1장 이상 첨부했는지?
//        if(postProductReq.getProductImgs().size() < 1)
//            return new BaseResponse<>(EMPTY_PRODUCT_IMG);

        try {
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (postProductReq.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PostProductRes postProductRes = productService.postProducts(postProductReq.getUserIdx(), postProductReq);
            return new BaseResponse<>(postProductRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /** 상품 수정 api
    *
     * @param
     * @param productIdx
     * @return
     */
    @ResponseBody
    @PatchMapping("/{productIdx}") // (PATCH) 127.0.0.1:9000/products/:productIdx
    public BaseResponse<PatchProductRes> patchProduct(@RequestBody PatchProductReq patchProductReq, @PathVariable("productIdx") int productIdx){
        try {
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (patchProductReq.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }


            PatchProductRes patchProductRes = productService.patchProduct(productIdx, patchProductReq);
            return new BaseResponse<>(patchProductRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

        /** 상품 판매상태 변경 api
         *
         * @param productIdx
         * @param saleStatus
         * @return
         */
        @ResponseBody
        @PatchMapping("") // (PATCH) 127.0.0.1:9000/products?productIdx={productIdx}&saleStatus={saleStatus}
        public BaseResponse<PatchProductRes> patchSaleStatus (@RequestParam("productIdx") int productIdx, @RequestParam("saleStatus") String saleStatus, @RequestBody ProductUserIdx productUserIdx){
            try {
                // 회원용 API
                int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
                if (productUserIdx.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
                PatchProductRes patchProductRes = productService.patchSaleStatus(productIdx, saleStatus);
                return new BaseResponse<>(patchProductRes);

            } catch (BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
        }

        /** 상품 삭제 api
         *
         * @param productIdx
         * @return
         */
        @ResponseBody
        @PatchMapping("/{productIdx}/status") // (PATCH) 127.0.0.1:9000/products/:productIdx/status
        public BaseResponse<PatchProductRes> deleteProduct ( @PathVariable("productIdx") int productIdx, @RequestBody ProductUserIdx productUserIdx){
            try {
                // 회원용 API
                int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
                if (productUserIdx.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                    return new BaseResponse<>(INVALID_USER_JWT);
                }

                PatchProductRes patchProductRes = productService.deleteProduct(productIdx);
                return new BaseResponse<>(patchProductRes);

            } catch (BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
        }

        /** 태그 검색 api
         *
         * @param tag
         * @return
         */
        @ResponseBody
        @GetMapping("") // (GET) 127.0.0.1:9000/products?tag={tag}
        public BaseResponse<List<GetProductList>> getProductByTag (@RequestParam("tag") String tag){
            try {
                List<GetProductList> getProductList = productProvider.getProductsByTag(tag);
                return new BaseResponse<>(getProductList);

            } catch (BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
        }

        /** 상품 리스트 뽑아내기 api (홈화면 용)면
         *
         * @return
         */
        @ResponseBody
        @GetMapping("/home") // (GET) 127.0.0.1:9000/products/home
        public BaseResponse<List<GetProductList>> getProducts () {
            try {
                List<GetProductList> getProductList = productProvider.getHomeProducts();
                return new BaseResponse<>(getProductList);

            } catch (BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
        }

        /** 카테고리 별 상품 뽑아내기 api
         *
         * @param categoryIdx
         * @return
         */
        @ResponseBody
        @GetMapping("/category/{categoryIdx}") // (GET) 127.0.0.1:9000/products/category/1
        public BaseResponse<List<GetProductList>> getProductsByCat (@PathVariable("categoryIdx") int categoryIdx){
            try {
                List<GetProductList> getProductList = productProvider.getProductsByCat(categoryIdx);
                return new BaseResponse<>(getProductList);

            } catch (BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
        }

        /** 서브카테고리 별 상품 뽑아내기 api
         *
         * @param subCategoryIdx
         * @return
         */
        @ResponseBody
        @GetMapping("/subCategory/{subCategoryIdx}") // (GET) 127.0.0.1:9000/products/subCategory/1
        public BaseResponse<List<GetProductList>> getProductsBySubCat (@PathVariable("subCategoryIdx") int subCategoryIdx){
            try {
                List<GetProductList> getProductList = productProvider.getProductsBySubCat(subCategoryIdx);
                return new BaseResponse<>(getProductList);

            } catch (BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
        }



}
