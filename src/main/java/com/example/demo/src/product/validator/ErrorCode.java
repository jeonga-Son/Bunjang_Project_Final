package com.example.demo.src.product.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(false,404,"PAGE NOT FOUND"),
    INTER_SERVER_ERROR(false,500,"INTER SERVER ERROR"),
    EMAIL_DUPLICATION(false,400,"EMAIL DUPLICATED"),
    ;
    private boolean isSuccess;
    private int code;

    private String message;

}
