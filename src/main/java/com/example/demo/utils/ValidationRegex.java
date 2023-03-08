package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
<<<<<<< HEAD
    public static boolean isRegexPhoneNO(String target) {
=======
    public static boolean isRegexEmail(String target) {
>>>>>>> 1879515 ([feat] 상품, 팔로우, 리뷰 API 추가)
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
}

