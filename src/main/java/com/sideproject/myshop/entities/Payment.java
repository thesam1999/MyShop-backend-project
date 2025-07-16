package com.sideproject.myshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {


    @Id
    @GeneratedValue
    private UUID id;

    //一個order有一個payment
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Order order;

    // 被淘汰舊的方法！
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(nullable = false)
//    private Date paymentDate;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;
}