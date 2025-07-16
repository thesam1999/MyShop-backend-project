package com.sideproject.myshop.auth.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.myshop.auth.dto.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 未登入，請求資源時，預設後端跳到登入畫面，不是和前後端分離設計()
// 自訂處理「未認證（unauthenticated）」的請求時，伺服器的回應行為
@Component
@RequiredArgsConstructor
public class RESTAuthenticationEntryPoint implements AuthenticationEntryPoint { // AuthenticationEntryPoint 介面定義：當一個未認證的請求想訪問受保護資源時，要怎麼做回應。

    private final ObjectMapper objectMapper;

//    private  ObjectMapper objectMapper = new ObjectMapper();


    // 只有一個主要方法
    // 這個方法會在當使用者未登入或認證失敗時被呼叫。
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // 把未授權的錯誤訊息加進DTO
        ApiErrorResponse apiError = new ApiErrorResponse( HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                authException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        // 指定狀態碼給前端
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 使用 Jackson 的 ObjectMapper 把 apiError 轉成 JSON 字串，寫入回應中。
        // response.getWriter() 讓我們把資料寫進HTTP回應主體(Body)
        objectMapper.writeValue(response.getWriter(), apiError);

    }
}