package com.sideproject.myshop.services;

import com.sideproject.myshop.dto.ProductDto;
import com.sideproject.myshop.entities.Product;

import java.util.List;
import java.util.UUID;


//interface的方法預設是public abstract所以可以省略
public interface ProductService {

    Product addProduct(ProductDto product);

    List<ProductDto> getAllProducts(UUID categoryId, UUID typeId);

    ProductDto getProductBySlug(String slug);

    Product fetchProductById(UUID uuid);

    ProductDto getProductById(UUID id);

    Product updateProduct(ProductDto productDto, UUID id);



}
