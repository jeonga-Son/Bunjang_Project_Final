package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static java.util.Objects.isNull;

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

    public ProductController(ProductProvider productProvider, ProductService productService, JwtService jwtService){
        this.productProvider = productProvider;
        this.productService = productService;
        this.jwtService = jwtService;
    }

    /** 특정 상품 조회 API
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



    /** 상품 등록 api
     *
     * @param postProductReq
     * @return
     */
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/products
    public BaseResponse<PostProductRes> postProductRes(@RequestBody PostProductReq postProductReq) throws BaseException {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (postProductReq.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }


            // validation : 이미지가 1장 이상인지?
            if (postProductReq.getProductImgs().size() < 1)
                return new BaseResponse<>(EMPTY_PRODUCT_IMG);

            // validation : 상품 이름을 적었는지?
            if(postProductReq.getProductName() == null)
                return new BaseResponse<>(EMPTY_PRODUCT_NAME);

            // validation : 상품 이름이 40자 이상인지?
            if(postProductReq.getProductName().length() > 40)
                return new BaseResponse<>(INVALID_PRODUCT_NAME);

            // validation : 상품 가격이 500~999,999,999원 인지?
            if(postProductReq.getPrice() < 500 || postProductReq.getPrice() > 999999999)
                return new BaseResponse<>(INVALID_PRODUCT_PRICE);

            // validation : 내용을 적었는지?
            if(postProductReq.getDescription() == null)
                return new BaseResponse<>(EMPTY_PRODUCT_DESCRIPTION);

            // validation : 내용이 10자 이상 21844자 미만인지?
            if(postProductReq.getDescription().length() < 10 || postProductReq.getDescription().length() > 20000)
                return new BaseResponse<>(INVALID_PRODUCT_DESCRIPTION);

            // validation : 태그가 5개 이하인지?
            if(postProductReq.getTags().size()  > 5)
                return new BaseResponse<>(INVALID_PRODUCT_TAGS);

            // validation : 이미지 사이즈가 640 X 640 이하인가?
            for (PostProductImgs postProductImgs : postProductReq.getProductImgs()) {
                String imgUrl = postProductImgs.getProductImgUrl();
                URL url = new URL(imgUrl);
                BufferedImage bi = ImageIO.read(url);
                int width = bi.getWidth();
                int height = bi.getHeight();

                if (width > 640 && height > 640)
                    return new BaseResponse<>(INVALID_PRODUCT_IMG);
            }


            PostProductRes postProductRes = productService.postProducts(postProductReq.getUserIdx(), postProductReq);
            return new BaseResponse<>(postProductRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** 상품 수정 api
     *
     * @param patchProductReq
     * @param productIdx
     * @return
     */
    @ResponseBody
    @PatchMapping("/{productIdx}") // (PATCH) 127.0.0.1:9000/products/:productIdx
    public BaseResponse<PatchProductRes> patchProduct(@RequestBody PatchProductReq patchProductReq, @PathVariable("productIdx") int productIdx) {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (patchProductReq.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // validaiton : 이미지 객체가 공란인지?
            if (patchProductReq.getProductImgs() == null) // 이건 객체가 있는지 확인, 즉 json 파일 상에서 해당 항목이 아예 없는 경우
                return new BaseResponse<>(EMPTY_PRODUCT_IMG);

            // validation : (이미지 객체가 공란은 아니지만) 이미지가 1장 이상인지?
            if (patchProductReq.getProductImgs().size() < 1) // 이건 해당 항목이 있지만, 공란인 경우
                return new BaseResponse<>(EMPTY_PRODUCT_IMG);


            // validation : 상품 이름을 적었는지?
            if(patchProductReq.getProductName() == null)
                return new BaseResponse<>(EMPTY_PRODUCT_NAME);

            // validation : 상품 이름이 40자 이상인지?
            if(patchProductReq.getProductName().toString().length() > 40 || patchProductReq.getProductName().toString().length() < 1)
                return new BaseResponse<>(INVALID_PRODUCT_NAME);

            // validation : 서브카테고리 id를 적었는지? int 형은 null을 가질 수 없기때문에 null 검사를 못한다...
            if((isNull(patchProductReq.getSubCategoryIdx())))
                return new BaseResponse<>(INVALID_PRODUCT_NAME);

            // validation : 상품 가격이 500~999,999,999원 인지?
            if(patchProductReq.getPrice() < 500 || patchProductReq.getPrice() > 999999999)
                return new BaseResponse<>(INVALID_PRODUCT_PRICE);

            // validation : 내용을 적었는지?
            if(patchProductReq.getDescription() == null)
                return new BaseResponse<>(EMPTY_PRODUCT_DESCRIPTION);

            // validation : 내용이 10자 이상 21844자 미만인지?
            if(patchProductReq.getDescription().length() < 10 || patchProductReq.getDescription().length() > 20000)
                return new BaseResponse<>(INVALID_PRODUCT_DESCRIPTION);

            // validation : 태그가 5개 이하인지?
            if(patchProductReq.getTags().size()  > 5)
                return new BaseResponse<>(INVALID_PRODUCT_TAGS);

            // validation : 이미지 사이즈가 640 X 640 이하인가?
            for (PostProductImgs postProductImgs : patchProductReq.getProductImgs()) {
                String imgUrl = postProductImgs.getProductImgUrl();
                URL url = new URL(imgUrl);
                BufferedImage bi = ImageIO.read(url);
                int width = bi.getWidth();
                int height = bi.getHeight();

                if (width > 640 && height > 640)
                    return new BaseResponse<>(INVALID_PRODUCT_IMG);
            }

            PatchProductRes patchProductRes = productService.patchProduct(productIdx, patchProductReq);
            return new BaseResponse<>(patchProductRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    public BaseResponse<PatchProductRes> patchSaleStatus(@RequestParam("productIdx") int productIdx,
                                                         @RequestParam("saleStatus") String saleStatus,
                                                         @RequestBody ProductUserIdx productUserIdx) {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (productUserIdx.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchProductRes patchProductRes = productService.patchSaleStatus(productIdx, saleStatus);
            return new BaseResponse<>(patchProductRes);

        } catch(BaseException exception){
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
    public BaseResponse<PatchProductRes> deleteProduct(@PathVariable("productIdx") int productIdx,
                                                       @RequestBody ProductUserIdx productUserIdx) {
        try{
            // 회원용 API
            int userIdxByJwt = jwtService.getUserIdx(); // jwt에서 userIdx 추출
            if (productUserIdx.getUserIdx() != userIdxByJwt) { // 유저가 제시한 userIdx != jwt에서 추출한 userIdx
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchProductRes patchProductRes = productService.deleteProduct(productIdx);
            return new BaseResponse<>(patchProductRes);

        } catch(BaseException exception){
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
    public BaseResponse<List<GetProductList>> getProductByTag (@RequestParam("tag") String tag) {
        try{
            List<GetProductList> getProductList = productProvider.getProductsByTag(tag);
            return new BaseResponse<>(getProductList);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 상품 리스트 뽑아내기 api (홈화면 용)
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/home") // (GET) 127.0.0.1:9000/products/home
    public BaseResponse<List<GetProductList>> getProducts () {
        try{
            List<GetProductList> getProductList = productProvider.getProducts();
            return new BaseResponse<>(getProductList);

        } catch(BaseException exception){
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
    public BaseResponse<List<GetProductList>> getProductsByCat (@PathVariable("categoryIdx") int categoryIdx) {
        try{
            List<GetProductList> getProductList = productProvider.getProductsByCat(categoryIdx);
            return new BaseResponse<>(getProductList);

        } catch(BaseException exception){
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
    public BaseResponse<List<GetProductList>> getProductsBySubCat (@PathVariable("subCategoryIdx") int subCategoryIdx) {
        try{
            List<GetProductList> getProductList = productProvider.getProductsBySubCat(subCategoryIdx);
            return new BaseResponse<>(getProductList);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }









}
