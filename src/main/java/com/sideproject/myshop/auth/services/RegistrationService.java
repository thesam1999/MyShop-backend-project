package com.sideproject.myshop.auth.services;

import com.sideproject.myshop.auth.dto.RegistrationRequest;
import com.sideproject.myshop.auth.dto.RegistrationResponse;
import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.auth.helper.VerificationCodeGenerator;
import com.sideproject.myshop.auth.repositories.UserDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserDetailRepository userDetailRepository;

    private final AuthorityService authorityService;

    //    private final BCryptPasswordEncoder passwordEncoder;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;


    public RegistrationResponse createUser(RegistrationRequest request) {

        /*
        - RegistrationRequest 自訂資料傳輸物件（DTO)
        用來接收前端資料，避免前端直接操作資料庫模型（Entity），避免緊耦合

        流程步驟：
        1. 檢查使者是否存在，
            -已存在，且email認證過code400！
            -已存在，但為認證過，重新認證
        2. 把前端傳來的資料存進資料庫
        3. 傳送驗證碼到e-mail，同時把驗證碼存到資料庫
        4. 賦予使用者使用權限(這裡hard code都給user跟Admin)

         */


        User existing = userDetailRepository.findByEmail(request.getEmail());

        //帳號存在，返回 bad request
        if(existing != null){

            if (existing.isEnabled()) {
                return RegistrationResponse.builder()
                        .code(400)
                        .message("Email already exist!")
                        .build();
            }else {
                // 未啟用，重新產生驗證碼，更新資料庫驗證碼
                String newCode = VerificationCodeGenerator.generateCode();
                existing.setVerificationCode(newCode);
                userDetailRepository.save(existing);

                // 發送驗證碼
                emailService.sendMail(existing);

                return RegistrationResponse.builder()
                        .code(200)
                        .message("Email already registered but not verified. Verification email resent.")
                        .build();

            }
        }

        try{
            // 舊的寫法
//            User user = new User();
//            user.setFirstName(request.getFirstName());
//            user.setLastName(request.getLastName());
//            user.setEmail(request.getEmail());
//            user.setEnabled(false);
//
//            user.setPassword(passwordEncoder.encode(request.getPassword()));
//            user.setProvider("manual");

            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .enabled(false) //e-mail驗證成功，才會變成true
                    //用我在 WebSecurityConfig 設定的加密方式進行加密，預設是bcrypt
                    .password(passwordEncoder.encode(request.getPassword()))
                    .provider("manual")
                    .build();

            //生成驗證碼，並發送到email給用戶進行註冊認證
            String code = VerificationCodeGenerator.generateCode();

            // 也可以另外創一個table，專門存放驗證碼，但只是練習就不創了
            user.setVerificationCode(code);
            // 練習，所以這裡用hard code
            user.setAuthorities(authorityService.getUserAuthority());

            userDetailRepository.save(user);

            // 發送信箱驗證
            String result = emailService.sendMail(user);
            System.out.println(result);

            return RegistrationResponse.builder()
                    .code(200)
                    .message("User created!")
                    .build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException(e.getMessage(),e.getCause());
        }
    }

    // 把資料庫更新為驗證成功
    public void verifyUser(String userName) {
        User user= userDetailRepository.findByEmail(userName);
        user.setEnabled(true);
        userDetailRepository.save(user);
    }
}