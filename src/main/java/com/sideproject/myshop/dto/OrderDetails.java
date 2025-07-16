package com.sideproject.myshop.dto;

import com.sideproject.myshop.entities.Address;
import com.sideproject.myshop.entities.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetails {

    private UUID id;
    private LocalDateTime orderDate;
    private Address address;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private String shipmentNumber;
    private LocalDateTime expectedDeliveryDate;
    private List<OrderItemDetail> orderItemList;

}