package com.sideproject.myshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sideproject.myshop.auth.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    //舊的過時用法
//    @Temporal(TemporalType.TIMESTAMP) //  JPA 的註解，用來告訴它應該如何處理 Java 的 Date 類別
//    private Date orderDate;

    //儲存包含時間和日期！
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    // 預設指向User的主鍵，可以用 referencedColumnName 指定User的其他欄位
    @JoinColumn(name = "user_id",nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id",nullable = true)
    @ToString.Exclude
    @JsonIgnore
    private Address address;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING) //指定Enum如何儲存到資料庫中，預設是儲存數字(enum的序列位置)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = true)
    private String shipmentTrackingNumber;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expectedDeliveryDate;

    // 一個order可以有多個orderItem，像是一個訂單可以包含多個商品
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "order",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderItem> orderItemList;

    private Double discount;

    // 可以不用額外設定一個Payment，但分開設定，可以把支付相關的屬性跟訂單訊息分開，讓不同class專注自己的領域，符合單一責任原則！
    // fetch 要在擁有方設定，因此我這裡設定不會改變任何東西！
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "order",cascade = CascadeType.ALL)
    @ToString.Exclude
    private Payment payment;

}