package com.sideproject.myshop.dto;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    // UUID 是一種專門用來產生「全球唯一的識別碼」的型別
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String brand;
    private boolean isNewArrival;
    private Float rating;
    private UUID categoryId;
    private String thumbnail;
    private String slug;
    private String categoryName;
    private UUID categoryTypeId;
    private String categoryTypeName;
    private List<ProductVariantDto> variants;
    private List<ProductResourceDto> productResources;


}
