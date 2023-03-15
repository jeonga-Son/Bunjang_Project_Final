package com.example.demo.src.product.validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private boolean isSuccess;
    private int code;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
    }
}