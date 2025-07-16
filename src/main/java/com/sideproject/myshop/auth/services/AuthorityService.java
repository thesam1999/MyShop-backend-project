package com.sideproject.myshop.auth.services;

import com.sideproject.myshop.auth.entities.Authority;
import com.sideproject.myshop.auth.repositories.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// 負責創建、取得用戶權限
@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;


    // 取得用戶權限
    public List<Authority> getUserAuthority(){

        List<Authority> authorities=new ArrayList<>();
        // 練習，所以這裡用hard code
        Authority authority= authorityRepository.findByRoleCode("USER");
        Authority authority2= authorityRepository.findByRoleCode("ADMIN");
        authorities.add(authority);
//        authorities.add(authority2);
        return authorities;
    }

    public Authority createAuthority(String role, String description){
        if (authorityRepository.existsByRoleCode(role)) {
            throw new RuntimeException("權限代碼已存在");
        }

        Authority authority= Authority.builder().
                roleCode(role).
                roleDescription(description).
                build();

        return authorityRepository.save(authority);
    }
}
