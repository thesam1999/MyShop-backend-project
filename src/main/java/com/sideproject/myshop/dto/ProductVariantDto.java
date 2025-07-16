package com.sideproject.myshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDto {

    private UUID id;
    private String color;
    private String size;
    private Integer stockQuantity;
}
