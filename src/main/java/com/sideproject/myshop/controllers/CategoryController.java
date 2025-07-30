package com.sideproject.myshop.controllers;

import com.sideproject.myshop.dto.CategoryDto;
import com.sideproject.myshop.entities.Category;
import com.sideproject.myshop.services.CategoryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// category： 男生女生
// category_type：商品類別

@RestController
@RequestMapping("api/category")
@CrossOrigin
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<Category> setCategoryById(@PathVariable(value = "id", required = true)UUID categoryId){//一定要傳id否則抱錯
        Category category = categoryService.getCategory(categoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(HttpServletResponse response){
        List<Category> categoryList = categoryService.getAllCategory();
        response.setHeader("Content-Range",String.valueOf(categoryList.size())); //  React Admin需要的，有多少類型就回傳幾筆資料
        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto){
        Category category = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable(value = "id", required = true)UUID categoryId){
        Category updateCategory = categoryService.updateCategory(categoryDto, categoryId);

        return  new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable(value = "id", required = true)UUID categoryId){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }
}
