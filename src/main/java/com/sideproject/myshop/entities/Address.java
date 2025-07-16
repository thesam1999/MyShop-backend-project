package com.sideproject.myshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sideproject.myshop.auth.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    @JsonIgnore
    //避免 toString() 無限遞迴，這樣Log跟除錯輸出，不會顯示出來！
    @ToString.Exclude // 不讓這個欄位出現在toString()的結果
    private User user;

}