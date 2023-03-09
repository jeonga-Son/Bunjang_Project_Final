package com.example.demo.src.product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        try{
            List<GetProductList> getProductList = productProvider.getProducts(productIdx);
            List<GetReviewList> getReviewList = productProvider.getReviews(productIdx);
            GetShopInfo getShopInfo = productProvider.getShopInfo(productIdx);
            GetShopRes getShopRes = new GetShopRes(getReviewList,getProductList,getShopInfo);

            GetProductInfoRes getProductInfoRes = productProvider.getProductInfoRes(productIdx);
            GetProductRes getProductRes = new GetProductRes(getProductInfoRes, getShopRes);
            return new BaseResponse<>(getProductRes);
        } catch(BaseException exception){
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
    public BaseResponse<PostProductRes> postProductRes(@RequestBody PostProductReq postProductReq) {
        try{
            PostProductRes postProductRes = productService.postProducts(postProductReq.getUserIdx(), postProductReq);
            return new BaseResponse<>(postProductRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
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
            PatchProductRes patchProductRes = productService.patchProduct(productIdx, patchProductReq);
            return new BaseResponse<>(patchProductRes);

        } catch(BaseException exception){
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
    public BaseResponse<PatchProductRes> patchSaleStatus(@RequestParam("productIdx") int productIdx, @RequestParam("saleStatus") String saleStatus) {
        try{
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
    public BaseResponse<PatchProductRes> deleteProduct(@PathVariable("productIdx") int productIdx) {
        try{
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
    @GetMapping("") // (PATCH) 127.0.0.1:9000/products?tag={tag}
    public BaseResponse<List<GetProductList>> getProductByTag (@RequestParam("tag") String tag) {
        try{
            List<GetProductList> getProductList = productProvider.getProductsByTag(tag);
            return new BaseResponse<>(getProductList);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 상품 리스트 뽑아내기 api
    @ResponseBody
    @GetMapping("/home") // (PATCH) 127.0.0.1:9000/products/home
    public BaseResponse<List<GetProductList>> getProducts () {
        try{
            List<GetProductList> getProductList = productProvider.getProducts();
            return new BaseResponse<>(getProductList);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }







}
