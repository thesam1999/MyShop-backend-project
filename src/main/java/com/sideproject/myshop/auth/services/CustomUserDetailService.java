package com.sideproject.myshop.auth.services;

import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.auth.repositories.UserDetailRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 自定義「如何從資料庫中查詢使用者資料」的邏輯，並提供給 Spring Security 的登入驗證流程使用。
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserDetailRepository userDetailRepository;

    public CustomUserDetailService(UserDetailRepository userDetailRepository) {
        this.userDetailRepository = userDetailRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userDetailRepository.findByEmail(username);

        if (null == user)
            throw new UsernameNotFoundException("User Not Found with userName" + username);

        return  user;
    }
}
