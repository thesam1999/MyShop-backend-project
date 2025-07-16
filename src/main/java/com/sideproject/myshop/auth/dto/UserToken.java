package com.sideproject.myshop.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//封裝回傳給前端的資料，除了jwt token，我也可以自己拓展
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {

    private String token;
}
