package com.sideproject.myshop.auth.helper;

import java.util.Random;

// 生成六位數的隨機驗證碼
public class VerificationCodeGenerator {

    public static String generateCode(){

        // 可以使用 ThreadLocalRandom 代替 Random！！
        Random random=new Random();

        // 生成一個範圍在 100000 到 999999 之間的隨機數，加上100000 保證了生成的數字是六位數
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}