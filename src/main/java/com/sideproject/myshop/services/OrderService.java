package com.sideproject.myshop.services;

import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.dto.OrderDetails;
import com.sideproject.myshop.dto.OrderItemDetail;
import com.sideproject.myshop.dto.OrderRequest;
import com.sideproject.myshop.dto.OrderResponse;
import com.sideproject.myshop.entities.*;
import com.sideproject.myshop.exceptions.InternalServerException;
import com.sideproject.myshop.exceptions.PaymentFailedException;
import com.sideproject.myshop.exceptions.ResourceNotFoundEx;
import com.sideproject.myshop.mapper.OrderMapper;
import com.sideproject.myshop.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserDetailsService userDetailsService;

    private final OrderMapper orderMapper;

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final PaymentIntentService paymentIntentService;


    // 確保資料原子性，如果這個方法會儲存多筆資料，如果其中一筆資料出錯，整筆資料都不會寫進資料庫！
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Principal principal) {

        // 從登入者的帳號取得 User 實體。
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

       //根據使用者提供的 addressId，在使用者的地址清單中找出相符的地址。
        //找不到就丟出 找不到資源。
        Address address = user.getAddressList().stream()
                .filter(address1 -> orderRequest.getAddressId().equals(address1.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundEx("找不到符合的地址 ID：" + orderRequest.getAddressId()));

//        // 建立訂單物件 Order
        Order order = orderMapper.mapOrderRequestToOrder(orderRequest, user, address);

        // 把前端的OrderItemRequest轉換成OrderItem
        //對每個 OrderItemRequest：
        //  取得商品資訊 Product。
        //  建立 OrderItem（每筆訂單項目對應一個商品 + 數量）。
        //  將 OrderItem 指向 order（建立關聯）。
        List<OrderItem> orderItems = orderMapper.mapOrderItemRequestsToEntities(orderRequest, order, productService);
        // 把商品清單設定到訂單內部欄位。
        order.setOrderItemList(orderItems);

        //建立付款物件 Payment。預設付款狀態是 PENDING。並與訂單做關聯。
        Payment payment = orderMapper.mapToPayment(order);
        order.setPayment(payment);

        //  儲存整張訂單
        Order savedOrder = orderRepository.save(order);

        // 回傳的 OrderResponse 會包含訂單編號與付款方式。
        OrderResponse orderResponse = OrderResponse.builder()
                .paymentMethod(orderRequest.getPaymentMethod())
                .orderId(savedOrder.getId())
                .build();

        // 若使用者選擇信用卡付款，會透過 Stripe 建立付款（PaymentIntent），並將資訊存入 credentials。
        if(Objects.equals(orderRequest.getPaymentMethod(), "CARD")){
            try {
                orderResponse.setCredentials(paymentIntentService.createPaymentIntent(order));
            } catch (StripeException e) {
                // 包成自己的錯誤格式（例如：BadRequestException）
                throw new PaymentFailedException("建立付款失敗: " + e.getMessage(), e);
            }
        }
        return orderResponse;
    }



    // stripe 付款相關的
    public Map<String,String> updateStatus(String paymentIntentId, String status) {

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if (paymentIntent == null) {
                throw new IllegalArgumentException("PaymentIntent not found");
            }

            if(!paymentIntent.getStatus().equals("succeeded")){
                throw new PaymentFailedException("PaymentIntent status is not succeeded");
            }

            String orderIdStr = paymentIntent.getMetadata().get("orderId");

            if (orderIdStr == null) {
                throw new BadRequestException("Missing OrderIdStr  in PaymentIntent metadata");
            }

            UUID orderId = UUID.fromString(orderIdStr);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + orderId));

            updatePayment(order, orderId, paymentIntent);
            // 儲存付款方式， 可以把這個 PaymentMethod ID 儲存在資料庫，讓使用者下次購物可以直接使用這個付款方式（不用重新輸入卡號）。
            order.setPaymentMethod(paymentIntent.getPaymentMethod());
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
            // 我只是更改原本就已經關聯的Payment，所以不用再set一次，因為本來就關聯了，所以Payment更改，但id不變，就不用特別再設定一次
//            order.setPayment(payment);

            Order savedOrder = orderRepository.save(order);

            Map<String,String> map = new HashMap<>();
                map.put("orderId", String.valueOf(savedOrder.getId()));

            return map;

        }catch (StripeException e) {
            // 針對 Stripe API 例外可作特殊處理或記錄
            throw new PaymentFailedException("Stripe error: " + e.getMessage(), e);
        }catch (Exception e){
            // 其他例外
            throw new InternalServerException("Error updating payment status: " + e.getMessage(), e);
        }
    }

    private static void updatePayment(Order order, UUID orderId, PaymentIntent paymentIntent) throws BadRequestException {
        Payment payment = order.getPayment();
        if (payment == null) {
            throw new ResourceNotFoundEx("Payment entity not found for order: " + orderId);
        }

        String paymentMethod = paymentIntent.getPaymentMethod();
        if (paymentMethod == null) {
            throw new BadRequestException("Payment method is missing in PaymentIntent");
        }

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMethod(paymentIntent.getPaymentMethod());
    }


    // 用username，把資料庫的的Order資料傳出來
    public List<OrderDetails> getOrdersByUser(String name) {
        User user = (User) userDetailsService.loadUserByUsername(name);
        List<Order> orders = orderRepository.findByUser(user);

        // 對 orders 這個 List<Order> 每一筆 order 做處理
        //把每個 Order 轉換成一個 OrderDetails（這是前端要用的格式）
        return orders.stream()
                .map(order -> OrderDetails.builder()
                        .id(order.getId())
                        .orderDate(order.getOrderDate())
                        .orderStatus(order.getOrderStatus())
                        .shipmentNumber(order.getShipmentTrackingNumber())
                        .address(order.getAddress())
                        .totalAmount(order.getTotalAmount())
                        .orderItemList(getItemDetails(order.getOrderItemList()))
                        .expectedDeliveryDate(order.getExpectedDeliveryDate())
                        .build())
                .toList();

    }

    // 把orderItemList轉成OrderItemDetail (差在一個order！)
    private List<OrderItemDetail> getItemDetails(List<OrderItem> orderItemList) {

        return orderItemList.stream().map(orderItem -> {
            return OrderItemDetail.builder()
                    .id(orderItem.getId())
                    .itemPrice(orderItem.getItemPrice())
                    .product(orderItem.getProduct())
                    .productVariantId(orderItem.getProductVariantId())
                    .quantity(orderItem.getQuantity())
                    .build();
        }).toList(); //結果記得包裝回list！
    }

    // 取消訂單
    public void cancelOrder(UUID id, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + id));

        if(order.getUser().getId().equals(user.getId())){
            order.setOrderStatus(OrderStatus.CANCELLED);

            // TODO:退款到使用者帳號的邏輯

            orderRepository.save(order);
        }
        else{
            throw new RuntimeException("Invalid request");
        }
    }
}