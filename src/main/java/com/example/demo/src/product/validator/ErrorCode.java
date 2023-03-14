package com.example.demo.src.product.validator;

import lombok.Getter;

public enum ErrorCode {

    NOT_NULL(false,2030,"필수 값이 누락되었습니다")
    , MIN_VALUE(false,2031, "최소값보다 커야 합니다.")
    ;

    @Getter
    private boolean isSuccess;

    @Getter
    private int code;

    @Getter
    private String message;

    ErrorCode(boolean isSuccess, int code, String description) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = description;
    }
}