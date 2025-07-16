package com.sideproject.myshop.entities;

// 定義一個Java 的列舉(enum)
// 同一個 package用這個enum就不會import
public enum OrderStatus {
    PENDING,
    IN_PROGRESS,
    SHIPPED,
    DELIVERED,
    CANCELLED
}