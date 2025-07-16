package com.sideproject.myshop.auth.controller;

//import com.sideproject.myshop.auth.config.JWTTokenHelper;
import com.sideproject.myshop.auth.helper.JWTTokenHelper;
import com.sideproject.myshop.auth.dto.LoginRequest;
import com.sideproject.myshop.auth.dto.RegistrationRequest;
import com.sideproject.myshop.auth.dto.RegistrationResponse;
import com.sideproject.myshop.auth.dto.UserToken;
import com.sideproject.myshop.auth.entities.Authority;
import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.auth.services.AuthorityService;
import com.sideproject.myshop.auth.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RegistrationService registrationService;
    private final UserDetailsService userDetailsService;
    private final AuthorityService authorityService;
    private final JWTTokenHelper jwtTokenHelper;



    // 使用者透過 /login API 傳送帳號密碼，系統驗證後回傳 JWT Token（如果登入成功），否則回傳 401 Unauthorized。
    @PostMapping("/login")
    // 回傳自訂義UserToken，封裝登入成功後要回傳給前端的資料，也可以方便擴充回傳資料
    public ResponseEntity<UserToken> login(@RequestBody LoginRequest loginRequest){
        //自訂義LoginRequest: 封裝前端傳來的帳密，避免直接暴露物件，控制接收資料欄位

        /*
          驗證流程說明：
          1. 建立尚未驗證的 Authentication（用來封裝帳密）
          2. 使用 authenticationManager 驗證帳密
          3. 驗證成功 → 檢查帳號是否啟用 → 發 JWT token 給前端
         */


        try{
            /*
            創建一個未驗證的物件
             */
            //建立一個尚未驗證的 驗證物件，交給 AuthenticationManager 進行驗證
            // UsernamePasswordAuthenticationToken 是 Spring Security 預設的「帳號密碼驗證 Token」
            Authentication authentication= UsernamePasswordAuthenticationToken
                    .unauthenticated(loginRequest.getUserName(), loginRequest.getPassword());

            /*
            執行驗證
             */
            //authenticate()這個方法，會呼叫 AuthenticationManager（之前用 DaoAuthenticationProvider 設定的）去驗證帳密。
            //如果帳密正確，它會回傳一個 驗證成功的 Authentication 物件。 失敗會丟出 BadCredentialsException。
            Authentication authenticationResponse = this.authenticationManager.authenticate(authentication); // this可不加，強調用這個物件而已


            // 驗證通過，取得登入使用者資訊(Principal)
            if(authenticationResponse.isAuthenticated()){

                // getPrincipal() 方法返回與該身份驗證相關聯的實際使用者資料。
                //返回對象是UserDetails，這裡我定義User去實現
                User user= (User) authenticationResponse.getPrincipal();

                // 會發送驗證碼到email，如果沒驗證，enable會是false，返回unauthorized狀態
                if(!user.isEnabled()) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }

                String token =jwtTokenHelper.generateToken(user.getEmail());
                UserToken userToken= UserToken.builder()
                        .token(token)
                        .build();

                return new ResponseEntity<>(userToken,HttpStatus.OK);
            }

            // 如果使用者名稱跟密碼輸入錯誤，會返回unauthorized狀態
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request){

        /*
        RegistrationResponse自訂的回傳類別
        註冊成功code200，失敗會顯示400
        對應http狀態碼 OK or Bad_request
         */

        RegistrationResponse registrationResponse = registrationService.createUser(request);
        return new ResponseEntity<>(registrationResponse,
                // 三元運算子
                registrationResponse.getCode() == 200 ? HttpStatus.OK: HttpStatus.BAD_REQUEST);
    }

    // TODO： 新增刪除使用者API


    // 檢查註冊時信箱收到的驗證碼，和資料庫裡的驗證碼是否一樣
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String,String> map){ //Map存儲值對鍵的數據結構

        /*
        從前端接收 userName和驗證碼(code) JSON Map(key-value 格式)
        -> 根據 userName 從資料庫查詢對應的 User
        -> 比對資料庫中的驗證碼與前端輸入是否相符
        -> 若驗證通過，更新 User 狀態為已啟用(setEnabled改為true)
        -> 失敗回傳BAD_REQUEST
         */

        String userName = map.get("userName");
        String code = map.get("code");

        User user= (User) userDetailsService.loadUserByUsername(userName);

        // == 比較記憶體位置，.equal()是比較內容值是否相同
        if(user != null && user.getVerificationCode().equals(code)){
            // 把資料庫的User狀態更新為認證成功
            registrationService.verifyUser(userName);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/createAuth")
    public ResponseEntity<Authority> createAuth(@RequestBody Authority authority){

        Authority savedAuthority = authorityService.createAuthority(authority.getRoleCode(), authority.getRoleDescription());

        return new ResponseEntity<>(savedAuthority, HttpStatus.CREATED);
    }

}
