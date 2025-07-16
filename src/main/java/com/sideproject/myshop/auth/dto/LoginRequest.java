package com.sideproject.myshop.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//封裝前端傳來的資料，避免獲得多餘的資料
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String userName;
    // 泛用的字串表示法
    // 接收者可以靈活決定用 String 還是 StringBuilder
    //StringBuilder 可以在記憶體中清除內容（用完可以 .setLength(0)），而 String 在 JVM 裡是不可變的，無法清除。
//    private CharSequence password;
    // 用String比較好
    private String password;
}