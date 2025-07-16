package com.sideproject.myshop.auth.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

// 自訂的 JWT 驗證 token 類別
// 區分出「這是 JWT 登入來的」不是普通登入，方便在filter 中做 instanceof 判斷
public class JWTAuthenticationToken extends UsernamePasswordAuthenticationToken {


    public JWTAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public JWTAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
