package com.sideproject.myshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

// 在order跟product之間，多一個orderItem，多了更多靈活性跟擴展性，例如可以在OrderItem加上一個商品數量、折扣...等等資訊！！
// 一筆訂單裡面，某個商品的購買明細（商品種類、數量、價格）
@Entity
@Table(name="order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    // 一個OrderItem代表一個商品(product)，但一個OrderItem對應一個Order(訂單)，不同訂單可以對應同一個Product，像是不同訂單都買了apple這個商品
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    // 使用者一次只會選定一個Variant 或是不選
    private UUID productVariantId;

    @ManyToOne(fetch = FetchType.LAZY) //很多不同的商品都指定到一個訂單中
    @JoinColumn(name = "order_id",nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Order order;

    @Column(nullable = false)
    private Integer quantity;

    private BigDecimal itemPrice;
}

