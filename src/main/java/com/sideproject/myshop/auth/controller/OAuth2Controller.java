package com.sideproject.myshop.auth.controller;

import com.sideproject.myshop.auth.helper.JWTTokenHelper;
import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.auth.services.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/*
使用者google登入後要進行什麼操作：
從 OAuth2User 取得使用者帳號，
存在：導航前端API，回傳JWT
不存在：創一個新的帳號，執行上面操作
 */
@RestController
@CrossOrigin
@RequestMapping("/oauth2")
public class OAuth2Controller {

    final
    OAuth2Service oAuth2Service;

    private final JWTTokenHelper jwtTokenHelper;

    public OAuth2Controller(OAuth2Service oAuth2Service, JWTTokenHelper jwtTokenHelper) {
        this.oAuth2Service = oAuth2Service;
        this.jwtTokenHelper = jwtTokenHelper;
    }

    //當使用者經過 Google OAuth2 登入成功，會被 Spring Security 導向 /oauth2/success。(config設定的！)
    // @AuthenticationPrincipal OAuth2User oAuth2User：Spring Security 自動把目前登入的 Google 使用者資料帶進來。
    @GetMapping("/success")
    public void callbackOAuth2(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {

        String userName = oAuth2User.getAttribute("email");
        User user=oAuth2Service.getUser(userName);

        // 如果User不存在，就幫他註冊一個到資料庫！
        if(user == null){
            user = oAuth2Service.createUser(oAuth2User,"google");
        }

        String token = jwtTokenHelper.generateToken(user.getUsername());

        // 讓使用者在登入認證成功後，被重定向到前端應用程式的 URL，並附帶一個 token 參數。
        response.sendRedirect("http://localhost:3000/oauth2/callback?token="+token);

    }
}