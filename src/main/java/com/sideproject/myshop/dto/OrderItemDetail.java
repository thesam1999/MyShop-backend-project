package com.sideproject.myshop.dto;

import com.sideproject.myshop.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.rmi.server.UID;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDetail {

    private UUID id;
    private Product product;
    private UUID productVariantId;
    private Integer quantity;
    private BigDecimal itemPrice;
}