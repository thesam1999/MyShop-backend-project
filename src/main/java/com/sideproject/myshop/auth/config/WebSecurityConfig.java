package com.sideproject.myshop.auth.config;

import com.sideproject.myshop.auth.exceptions.RESTAuthenticationEntryPoint;
import com.sideproject.myshop.auth.helper.JWTTokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;

    private final RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] publicApis= {
            "/api/auth/**"
    };


    // 定義那些路由需要授權哪些可以公開訪問，以及配置 OAuth2 登入與 JWT 驗證邏輯。
    @Bean // 用在方法上，你自己用程式去「建物件」，然後 Spring 負責管理它
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 啟用 CORS（跨來源資源分享）功能，並使用 Spring Boot 提供的預設設定。
                .cors(Customizer.withDefaults()) //要加上搭配@CrossOrigin這個前端才不會被cors擋到
                .csrf(AbstractHttpConfigurer::disable) //關閉csrf，因為我用jwt驗證，這是無狀態的

                //設定session為stateless，不能跟Oauth一起用！Oauth 會失敗！
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )

                // 定義那些網址可以未登入被用戶存取
                .authorizeHttpRequests((authorize)-> authorize
                        //swagger是允許的
                        // 第一個：回傳整個 API 的描述檔（用 JSON 格式表示）。
                        // 第二個：載入 Swagger 的前端頁面，用來瀏覽和測試 API。(現在都用swagger-ui/index.html，所以其實可以省略)
                        // 第三個：Swagger UI 所需的靜態資源（JS、CSS、圖片等）會走這個路徑。
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        //對product跟category發送get是允許的
                        .requestMatchers(HttpMethod.GET,"/api/products","/api/category").permitAll()
                        // oauth成功後，會導入的網址
                        .requestMatchers("/oauth2/success").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/file").authenticated()
                        //先創鍵權限角色
                        .requestMatchers("/api/auth/createAuth").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // 其他請求要登入才能訪問
                        .anyRequest().authenticated())

                // 設定oauth登入方式，登入成功後會跳轉到 /oauth2/success
                .oauth2Login(// 開啟Oauth2.0登入功能（例如：Google 登入）
                        (oauth2login)-> oauth2login
                        .defaultSuccessUrl("/oauth2/success") //使用者透過 Google OAuth 成功登入後，會被 重新導向（redirect） 到 /oauth2/success 網址。
                        .loginPage("/oauth2/authorization/google")) // 使用者若尚未登入，系統會自動把他導向 /oauth2/authorization/google；spring boot會跳轉到google登入授權頁面，開始 OAuth 流程。

                // exceptionHandling() 這是HttpSecurity 的方法，用來設定「當認證/授權錯誤時該怎麼做」。
                .exceptionHandling((exception)-> exception
                        //用來處理「使用者未認證（unauthenticated）就存取需要授權的資源時，該怎麼辦」。
                        // 預設行為：跳轉到登入頁面。
                        // 這裡：自訂一個回傳 JSON 錯誤的方式，適合前後端分離的架構。
                        .authenticationEntryPoint(restAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    // 這裡改用permit all比較好！權限規則集中寫在同一個地方，清楚明瞭。
    //比 web.ignoring() 更安全，因為 web.ignoring() 會直接跳過整個安全過濾鏈，連日誌、記錄等機制都不走。

    // 宣告一個 WebSecurityCustomizer Bean，作用是告訴 Spring Security：
    //這些路徑（即 /api/auth/**）完全忽略，不經過 Spring Security filter chain。
    // 跟permitAll不一樣！！
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return (web) -> web.ignoring().requestMatchers(publicApis);
//    }


    // 處理 Spring Security 中的用戶身份驗證（authentication）流程，設置認證過程的細節，如果身份驗證成功，身份驗證提供者會返回一個 Authentication 物件，該物件包含已驗證的用戶資料（例如用戶的角色、權限等）
    @Bean
    public AuthenticationManager authenticationManager(){//是一個interface

        // DaoAuthenticationProvider 會從資料庫中根據用戶名查找用戶信息，然後將用戶提供的密碼與資料庫中的密碼進行比較，確保用戶身份的合法性。
        //需要配置：userDetailsService、passwordEncoder
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();

        //身份驗證提供者有很多種，這裡用DAO(Data Access Object)模式，讓我們可以把資料庫存取邏輯(UserDetailsService)，跟身分認證的邏輯分開來！實現解耦！！

        // 用 userDetailsService 查找用戶訊息
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        // 舊的方法
        //        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));

        //用passwordEncoder()對密碼進行編碼和驗證
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());


        // ProviderManager 是 AuthenticationManager 的一個具體實現。
        return new ProviderManager(daoAuthenticationProvider);

    }


    // 給spring security注入PasswordEncoder這個interface要使用的算法
    // 為了解決資料庫有多種加密算法的驗證，因此用createDelegatingPasswordEncoder()
    @Bean
    public PasswordEncoder passwordEncoder(){
        //DelegatingPasswordEncoder 是一個多策略的密碼加密器，支援多種不同的密碼編碼演算法（例如：bcrypt、pbkdf2、scrypt、argon2 等）
        // 它會根據存儲的密碼格式來自動選擇合適的加密算法。如果你存儲的密碼是用某種算法加密的，它會選擇相應的解密策略。
        // 比如密碼字串可能長這樣：{bcrypt}$2a$10$...，表示這是用 bcrypt 演算法編碼的密碼

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // 上面寫法，預設好常用的編碼器(bcrypt、pbkdf2、scrypt、argon2..)如果用 new DelegatingPasswordEncoder 要全部自己寫
    }
}
