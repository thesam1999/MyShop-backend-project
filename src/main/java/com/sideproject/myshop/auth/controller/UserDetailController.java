package com.sideproject.myshop.auth.controller;


import com.sideproject.myshop.auth.dto.UserDetailsDto;
import com.sideproject.myshop.auth.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


// 取得使用者的個人資料，並返回一個UserDetailsDto給前端
@RestController
@CrossOrigin
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserDetailController {

    private final UserDetailsService userDetailsService;

    @GetMapping("/profile")
    // 前端只需要傳送JWT Token，spring security會把使用者資訊存在SecurityContextHolder裡的SecurityContext，這個 context 中的 Authentication 物件有一個 getPrincipal() 方法，可以取得登入者資訊（通常是 UserDetails）
    //Principal 只有getName方法，如果要取得其他資訊要用別的方法！
    public ResponseEntity<UserDetailsDto> getUserProfile(Principal principal){
        // 取得使用者名稱
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        if(user == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .addressList(user.getAddressList())
                .authorityList(user.getAuthorities().toArray()).build();

        return new ResponseEntity<>(userDetailsDto, HttpStatus.OK);

    }
}