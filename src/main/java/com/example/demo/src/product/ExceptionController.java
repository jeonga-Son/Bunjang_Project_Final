//package com.example.demo.src.product;
//
//
//import com.example.demo.src.product.validator.EmptyPriceException;
//import com.example.demo.src.product.validator.ErrorCode;
//import com.example.demo.src.product.validator.ErrorResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//
//@Slf4j
//@RestControllerAdvice
//public class ExceptionController {
//    /**
//     * @valid 유효성체크에 통과하지 못하면  MethodArgumentNotValidException 이 발생한다.
//     */
//    @ExceptionHandler(EmptyPriceException.class)
//    public ResponseEntity<ErrorResponse> handleEmailDuplicateException(EmptyPriceException ex){
//        log.error("EmptyPriceException", ex);
//        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
//        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(Exception ex){
//        log.error("handleException",ex);
//        ErrorResponse response = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}