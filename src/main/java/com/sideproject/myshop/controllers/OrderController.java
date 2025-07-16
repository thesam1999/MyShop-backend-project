package com.sideproject.myshop.controllers;


import com.sideproject.myshop.dto.OrderDetails;
import com.sideproject.myshop.dto.OrderRequest;
import com.sideproject.myshop.dto.OrderResponse;
import com.sideproject.myshop.services.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
@RequiredArgsConstructor
public class OrderController {

    private final
    OrderService orderService;

    //  Principal principal 是 Spring Security 提供的物件，用來代表「目前登入的使用者」。
    // Spring 就會自動從 SecurityContextHolder 中的 Authentication 拿出使用者，然後幫你注入 Principal。
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, Principal principal){
        OrderResponse orderResponse = orderService.createOrder(orderRequest,principal);
        // return new ResponseEntity<>(order, HttpStatus.CREATED);
        // System.out.println(orderResponse);
        return new ResponseEntity<>(orderResponse,HttpStatus.CREATED);
    }

    // 更新付款狀態的請求
    @PostMapping("/update-payment")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String,String> request){
        Map<String,String> response = orderService.updateStatus(request.get("paymentIntent"),request.get("status")); 
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID id,Principal principal){
        orderService.cancelOrder(id,principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDetails>> getOrderByUser(Principal principal) {
        List<OrderDetails> orders = orderService.getOrdersByUser(principal.getName());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


}