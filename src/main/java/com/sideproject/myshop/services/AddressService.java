package com.sideproject.myshop.services;

import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.dto.AddressRequest;
import com.sideproject.myshop.entities.Address;
import com.sideproject.myshop.entities.Order;
import com.sideproject.myshop.repositories.AddressRepository;
import com.sideproject.myshop.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
//要搭配private final！
@RequiredArgsConstructor
public class AddressService {

    private final
    AddressRepository addressRepository;

    private final UserDetailsService userDetailsService;

    private final OrderRepository orderRepository;


    public Address createAddress(AddressRequest addressRequest, Principal principal){
        User user= (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = Address.builder()
                .name(addressRequest.getName())
                .street(addressRequest.getStreet())
                .city(addressRequest.getCity())
                .state(addressRequest.getState())
                .zipCode(addressRequest.getZipCode())
                .phoneNumber(addressRequest.getPhoneNumber())
                .user(user)
                .build();
        return addressRepository.save(address);
    }

    public void deleteAddress(UUID id) {
        // 先查找這筆地址
        Address address = addressRepository.getReferenceById(id);
        // 找出所有使用這筆 address 的訂單
        List<Order> orders = orderRepository.findByAddress(address);

        // 將每筆訂單的 address 設為 null
        for (Order order : orders) {
            order.setAddress(null);
        }

        // 儲存這些訂單的變更
        orderRepository.saveAll(orders);
        // 最後安全地刪除地址
        addressRepository.delete(address);
    }
}
