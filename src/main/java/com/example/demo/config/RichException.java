package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RichException extends Exception {
    private BaseResponseStatus status;


}
