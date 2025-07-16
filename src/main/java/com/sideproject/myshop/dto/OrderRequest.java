package com.sideproject.myshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private UUID userId;
    private LocalDateTime orderDate;
    private UUID addressId;
    // 取得Product的更多資料，像是ProductVariant
    private List<OrderItemRequest> orderItemRequests;
    private BigDecimal totalAmount;
    private Double discount;
    private String paymentMethod;
    private LocalDateTime expectedDeliveryDate;
}