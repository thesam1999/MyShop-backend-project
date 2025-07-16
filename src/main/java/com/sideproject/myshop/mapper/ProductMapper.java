package com.sideproject.myshop.mapper;

import com.sideproject.myshop.dto.ProductDto;
import com.sideproject.myshop.dto.ProductResourceDto;
import com.sideproject.myshop.dto.ProductVariantDto;
import com.sideproject.myshop.entities.*;
import com.sideproject.myshop.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryService categoryService;

    // 包含更新及創建product
    public Product mapProductDtoToEntity(ProductDto productDto){
        Product product = new Product();
        // 不是null代表是update
        if(productDto.getId() != null){
            product.setId(productDto.getId());
        }
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setBrand(productDto.getBrand());
        product.setNewArrival(productDto.isNewArrival());
        product.setPrice(productDto.getPrice());
        product.setRating(productDto.getRating());
        product.setSlug(productDto.getSlug());

        Category category = categoryService.getCategory(productDto.getCategoryId());
        if(category != null){
            product.setCategory(category);
            UUID categoryTypeId = productDto.getCategoryTypeId();

            CategoryType categoryType = category.getCategoryTypes().stream()
                    .filter(categoryType1 -> categoryType1.getId().equals(categoryTypeId))
                    .findFirst()
                    .orElse(null);
            product.setCategoryType(categoryType);
        }

        if(productDto.getVariants() != null){
            product.setProductVariants(mapProductVariantDtosToEntities(productDto.getVariants(),product));
        }

        if(productDto.getProductResources() != null){
            product.setResources(mapProductResourcesDtosToEntity(productDto.getProductResources(),product));
        }
        return product;
    }


    private List<Resources> mapProductResourcesDtosToEntity(List<ProductResourceDto> productResources, Product product) {
        return productResources.stream()
                .map(productResourceDto -> {
                    Resources resources= new Resources();
                    if(null != productResourceDto.getId()){
                        resources.setId(productResourceDto.getId());
                    }
                    resources.setName(productResourceDto.getName());
                    resources.setType(productResourceDto.getType());
                    resources.setUrl(productResourceDto.getUrl());
                    resources.setIsPrimary(productResourceDto.getIsPrimary());
                    resources.setProduct(product);
                    return resources;
                })
                .collect(Collectors.toList());
    }

    private List<ProductVariant> mapProductVariantDtosToEntities(List<ProductVariantDto> productVariantDtos, Product product){
        return productVariantDtos.stream()
                .map(productVariantDto -> {
                    ProductVariant productVariant = new ProductVariant();
                    if(productVariantDto.getId() != null){
                        productVariant.setId(productVariantDto.getId());
                    }
                    productVariant.setColor(productVariantDto.getColor());
                    productVariant.setSize(productVariantDto.getSize());
                    productVariant.setStockQuantity(productVariantDto.getStockQuantity());
                    productVariant.setProduct(product);
                    return productVariant;
                })
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductDtos(List<Product> products) {
//        把一個 Product 產品的 List，逐一轉換成 ProductDto（資料轉傳物件），然後回傳這個新的 List。
        return products.stream()
                .map(this::mapProductToDto)
                .toList();
        // this代表呼叫現在這個class！也就是ProductMapper
    }

    //轉換簡單基本欄位
    public ProductDto mapProductToDto(Product product) {

        return ProductDto.builder()
                .id(product.getId())
                .brand(product.getBrand())
                .name(product.getName())
                .price(product.getPrice())
                .isNewArrival(product.isNewArrival())
                .rating(product.getRating())
                .description(product.getDescription())
                .slug(product.getSlug())
                .thumbnail(getProductThumbnail(product.getResources()))
                .build();
    }

    //轉換複雜有關聯性的欄位
    public ProductDto mapProductToDto1(Product product) {
        ProductDto productDto = mapProductToDto(product);
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setCategoryTypeId(product.getCategoryType().getId());
        productDto.setVariants(mapProductVariantsToDtos(product.getProductVariants()));
        productDto.setProductResources(mapProductResourcesToDtos(product.getResources()));
        return productDto;
    }

    // 取得縮圖
    private String getProductThumbnail(List<Resources> resources) {
        return resources.stream()
                // 只留下IsPrimary = true
                .filter(Resources::getIsPrimary)
                //取出第一個
                .findFirst()
                // 如果有找到這個物件，呼叫它的 getUrl() 方法，取得該物件的圖片網址
                .map(Resources::getUrl) // 如果有找到主圖，就取 url
                .orElse("找不到圖片！因為你沒有設定isPrimary = true！！");   // 沒找到主圖，就給預設圖片
    }

    public List<ProductVariantDto> mapProductVariantsToDtos(List<ProductVariant> productVariants) {
        return productVariants.stream()
                .map(this::mapProductVariantToDto)
                .toList();
    }

    private ProductVariantDto mapProductVariantToDto(ProductVariant productVariant) {
        return ProductVariantDto.builder()
                .color(productVariant.getColor())
                .id(productVariant.getId())
                .size(productVariant.getSize())
                .stockQuantity(productVariant.getStockQuantity())
                .build();
    }

    public List<ProductResourceDto> mapProductResourcesToDtos(List<Resources> resources) {
        return resources.stream()
                .map(this::mapProductResourceToDto)
                .toList();
    }

    private ProductResourceDto mapProductResourceToDto(Resources resources) {
        return ProductResourceDto.builder()
                .id(resources.getId())
                .url(resources.getUrl())
                .name(resources.getName())
                .isPrimary(resources.getIsPrimary())
                .type(resources.getType())
                .build();
    }



}
