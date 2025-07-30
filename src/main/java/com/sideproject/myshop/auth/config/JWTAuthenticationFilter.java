package com.sideproject.myshop.auth.config;

import com.sideproject.myshop.auth.helper.JWTTokenHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// 一般創一個filter package專門放Filter
// 每個request都會執行這個class
// 讓我自訂的JWT驗證流程
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JWTTokenHelper jwtTokenHelper;


    // 自訂的JWT驗證流程
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //  檢查Header是否為Authentication，如果不是代表不用進行JWT認證，直接放行進入下一個filter
        // 檢查  是否已經完成JWT驗證(如果有就不用重新建一次Authentication，避免浪費資源)
        // 1️⃣ 讀取 Authorization 標頭的 JWT。
        // 2️⃣ 驗證這個 JWT 是否正確。
        // 3️⃣ 如果正確，就建立一個 UsernamePasswordAuthenticationToken，放進 SecurityContextHolder。

        String authHeader = request.getHeader("Authorization");

        // 如果沒有header，或是Header沒有Bearer，代表不需要進行JWT認證，這裡不是則攔截未授權的使用者(由 Spring Security 的 **授權規則authorizeHttpRequests()來決定)，因此可以直接放行進入下一個filter
        if(authHeader == null){
            filterChain.doFilter(request,response); // doFilter是進入下一個filter的意思
            return; // 終止程式
        }

        try{
            if(authHeader.startsWith("Bearer ")){

                //取得JWT
                String authToken = jwtTokenHelper.getToken(request);
                //獲得使用者名稱
                String userName = jwtTokenHelper.getUserNameFromToken(authToken);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                // 檢查是否有使用者名稱，且是否已經完成JWT驗證(如果有就不用重新見一次Authentication，避免浪費資源)
                if( userName != null && !(auth instanceof JWTAuthenticationToken)){
                    // 透過userName取得使用者資料
                    UserDetails userDetails= userDetailsService.loadUserByUsername(userName);

                    // 驗證JWT 是否有效
                    if(jwtTokenHelper.validateToken(authToken,userDetails)) {

                        //建立一個 Spring Security 用的身份驗證物件（Authentication），因為透過JWT認證，所以不用密碼
                        //因為下一個filter是UsernamePassword所以直接創建這個身份驗證物件（Authentication），讓他可以直接通過下一個UsernamePasswordFilter
                        JWTAuthenticationToken authenticationToken = new JWTAuthenticationToken(userDetails, "", userDetails.getAuthorities()); //第一個參數就是principal


                        // 把前端傳來的 request存進去 Authentication裡面
                        // authenticationToken.setDetails(new WebAuthenticationDetails(request));
                        // 跟上面功能一樣，但這是官方建議寫法，spring預設很多地方都會用到的類別
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        //把這個物件放進 SecurityContextHolder，代表「這個使用者現在是登入狀態」，以後的 filter 就能取得使用者資訊。
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }

            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}