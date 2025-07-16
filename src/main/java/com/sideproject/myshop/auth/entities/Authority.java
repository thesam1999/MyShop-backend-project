package com.sideproject.myshop.auth.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

//自訂義使用者的權限
@Table(name = "AUTH_AUTHORITY")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 自訂義使用者的權限，要實作GrantedAuthority 這個interface，讓Spring Security知道
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String roleCode;

    @Column(nullable = false)
    private String roleDescription;

    @Override
    public String getAuthority() {
        return roleCode;
    }
}