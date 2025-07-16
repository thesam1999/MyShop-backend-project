package com.sideproject.myshop.auth.services;

import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.auth.repositories.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service {

    final
    UserDetailRepository userDetailRepository;

    private final AuthorityService authorityService;

    public OAuth2Service(UserDetailRepository userDetailRepository, AuthorityService authorityService) {
        this.userDetailRepository = userDetailRepository;
        this.authorityService = authorityService;
    }

    public User getUser(String userName) {
        return userDetailRepository.findByEmail(userName);
    }

    public User createUser(OAuth2User oAuth2User, String provider) {
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String email = oAuth2User.getAttribute("email");
        User user= User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .provider(provider)
                .enabled(true)
                //我們這裡是用hard code寫USER權限的！
                .authorities(authorityService.getUserAuthority())
                .build();
        return userDetailRepository.save(user);
    }
}