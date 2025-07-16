package com.sideproject.myshop.auth.services;

import com.sideproject.myshop.auth.entities.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    // 從 application.properties 或 application.yml 檔案中讀取設定值，然後注入到變數 sender 裡。
    @Value("${spring.mail.username}") //讀取名為 spring.mail.username 的設定值。
    private String sender;


    // 用來發送驗證信給使用者的
    public String sendMail(User user){

        // 一般的寫法，不推薦，在性能和可讀性上有一些問題
//        String mailContent = "Hello " + user.getUsername() + ",\n";
//        mailContent += "Your verification code is: " + user.getVerificationCode() + "\n";
//        mailContent += "Please enter this code to verify your email.";
//        mailContent +="\n";
//        mailContent+= senderName;

        String subject = "Verify your email";
        String senderName = "MyShop";

        // Text Blocks（Java 13 及以上）
        String mailContent = """
                Hello %s,
                Your verification code is: %s
                Please enter this code to verify your email.
                %s
                """.formatted(user.getUsername(), user.getVerificationCode(), senderName);


        try{
            // 建立SimpleMailMessage 物件，用來表示一封簡單的電子郵件內容（包含寄件人、收件人、主旨、內容等）。
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            // 收件人
            mailMessage.setTo(user.getEmail());
            mailMessage.setText(mailContent);
            mailMessage.setSubject(subject);
            javaMailSender.send(mailMessage);
        }
        catch (Exception e){
            return "Error while Sending Mail: " + e.getMessage();
        }

        return "Email sent";
    }

}