package com.example.demo.src.product.validator;

import lombok.Getter;

@Getter
public class ErrorObject {
    private final String code;
    private final String fieldName;
    private final String errorMessage;

    public ErrorObject(String code, String fieldName, String errorMessage) {
        this.code=code;
        this.fieldName=fieldName;
        this.errorMessage=errorMessage;
    }

}
