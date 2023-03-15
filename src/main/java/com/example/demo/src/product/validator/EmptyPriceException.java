package com.example.demo.src.product.validator;

import lombok.Getter;

@Getter
public class EmptyPriceException extends RuntimeException{

    private ErrorCode errorCode;

    public EmptyPriceException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
