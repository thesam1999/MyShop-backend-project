package com.sideproject.myshop.controllers;

import com.sideproject.myshop.dto.ProductDto;
import com.sideproject.myshop.entities.Product;
import com.sideproject.myshop.services.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/products")
@CrossOrigin
@RequiredArgsConstructor
public class  ProductController {

    private final ProductService productService;

    @GetMapping
    // @RequestParam：查詢參數（Query Parameter）：?categoryId=...
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestParam(required = false)UUID  categoryId, // 商品分類(男生、女生)
                                                           @RequestParam(required = false,
                                                                   name = "typeId",
                                                                   value = "typeId" // name跟value效用一樣(寫一個就好)；會從 URL 的 ?categoryId=xxx 拿值
                                                           ) UUID typeId, // 商品類型(T-shirt, hoodie)
                                                           @RequestParam(required = false) String slug,
                                                           HttpServletResponse response){
        List<ProductDto> productList = new ArrayList<>();

        // StringUtils 用來處理字串（String）的各種常見操作，
        // 例如：判斷字串是否為空、去除空白、比較字串、字串轉換、字串的格式處理
        if(StringUtils.isNotBlank(slug)){
            ProductDto productDto = productService.getProductBySlug(slug);
            productList.add(productDto);
        }
        else {
            productList = productService.getAllProducts(categoryId, typeId);
        }
        // 將 productList 的大小（int 整數）轉成字串，作為標頭值。
        response.setHeader("Content-Range",String.valueOf(productList.size()));
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id){
        ProductDto productDto = productService.getProductById(id);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    // create Product
    //@RequestBody 接收前端傳來的body
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto){
        Product product = productService.addProduct(productDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductDto productDto,@PathVariable UUID id){
        Product product = productService.updateProduct(productDto,id);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

}
