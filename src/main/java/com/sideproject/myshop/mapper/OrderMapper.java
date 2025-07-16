package com.sideproject.myshop.mapper;

import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.dto.OrderRequest;
import com.sideproject.myshop.entities.*;
import com.sideproject.myshop.services.ProductService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order mapOrderRequestToOrder(OrderRequest orderRequest, User user, Address address) {
        return Order.builder()
                .user(user)
                .address(address)
                .totalAmount(orderRequest.getTotalAmount())
                .orderDate(orderRequest.getOrderDate())
                .discount(orderRequest.getDiscount())
                .expectedDeliveryDate(orderRequest.getExpectedDeliveryDate())
                .paymentMethod(orderRequest.getPaymentMethod())
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    public List<OrderItem> mapOrderItemRequestsToEntities(OrderRequest orderRequest, Order order, ProductService productService) {
        return orderRequest.getOrderItemRequests().stream()
                .map(orderItemRequest -> {
                    Product product = productService.fetchProductById(orderItemRequest.getProductId());

                    return OrderItem.builder()
                    .product(product)
                    .productVariantId(orderItemRequest.getProductVariantId())
                    .quantity(orderItemRequest.getQuantity())
                    .order(order)
                    .build();
        })
                .collect(Collectors.toList());
    }

    public Payment mapToPayment(Order order) {
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(order.getPaymentMethod());
        return payment;
    }
}
