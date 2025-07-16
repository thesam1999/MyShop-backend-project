package com.sideproject.myshop.services;

import com.sideproject.myshop.dto.ProductDto;
import com.sideproject.myshop.entities.*;
import com.sideproject.myshop.exceptions.ResourceNotFoundEx;
import com.sideproject.myshop.mapper.ProductMapper;
import com.sideproject.myshop.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.sideproject.myshop.repositories.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements  ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

//    //可以不用加上autowired 只有一個建構子的話spring 會自動幫我註冊成bran
//    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService, ProductMapper productMapper) {
//        this.productRepository = productRepository;
//        this.productMapper = productMapper;
//    }

    @Override
    public Product addProduct(ProductDto productDto) {
        Product product = productMapper.mapProductDtoToEntity(productDto);
        return productRepository.save(product);
    }

    @Override
    public List<ProductDto> getAllProducts(UUID categoryId, UUID typeId) {

        // Specification 是 Spring Data JPA 提供的用來動態組合查詢條件的工具。
        //這裡從空的條件開始（null 表示沒有任何條件），方便後續根據有沒有參數動態加條件。
        Specification<Product> productSpecification= Specification.where(null);

        if(categoryId != null){
            productSpecification = productSpecification.and(ProductSpecification.hasCategoryId(categoryId));
        }
        if(typeId != null){
            productSpecification = productSpecification.and(ProductSpecification.hasCategoryTypeId(typeId));
        }

        List<Product> products = productRepository.findAll(productSpecification);

        return productMapper.getProductDtos(products);
    }

    @Override
    public ProductDto getProductBySlug(String slug) {
        Product product= productRepository.findBySlug(slug);
        if(product == null){
            throw new ResourceNotFoundEx("Product Not Found for slug: " + slug);
        }
        return productMapper.mapProductToDto1(product);
    }

    // 給Order用的，從資料庫直接取得 Product 實體。
    // 使用時機：在內部業務邏輯中需要操作 Product 實體，如加入購物車、訂單綁定或修改後儲存。
    @Override
    public Product fetchProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundEx("Product Not Found!"));
    }

    // 根據產品 ID 取得產品詳細資訊，並轉換為 ProductDto。
    // 供 product Controller 層呼叫，回傳給前端使用。提供給前端 API 查詢產品詳情時用的。
    @Override
    public ProductDto getProductById(UUID id) {
        Product product = fetchProductById(id);
        return productMapper.mapProductToDto1(product);
    }

    @Override
    public Product updateProduct(ProductDto productDto, UUID id) {
        Product product= productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundEx("Product Not Found!"));
        //更新product，並把舊的id給更新的product上
        productDto.setId(product.getId());
        return productRepository.save(productMapper.mapProductDtoToEntity(productDto));
    }




}
