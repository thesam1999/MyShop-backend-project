package com.sideproject.myshop.controllers;

import com.sideproject.myshop.dto.AddressRequest;
import com.sideproject.myshop.entities.Address;
import com.sideproject.myshop.services.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/address")
@CrossOrigin
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody AddressRequest addressRequest, Principal principal){//Principal是取得登入驗證後的使用者資訊
        Address address = addressService.createAddress(addressRequest,principal);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id){// @PathVariable從 URL路徑 中抓變數
        addressService.deleteAddress(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}