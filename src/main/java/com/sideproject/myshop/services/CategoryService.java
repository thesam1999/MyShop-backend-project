package com.sideproject.myshop.services;

import com.sideproject.myshop.dto.CategoryDto;
import com.sideproject.myshop.entities.Category;
import com.sideproject.myshop.exceptions.ResourceNotFoundEx;
import com.sideproject.myshop.mapper.CategoryMapper;
import com.sideproject.myshop.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    public Category getCategory(UUID categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundEx("Category not found with Id :"+ categoryId));
    }

    public Category createCategory(CategoryDto categoryDto){
        Category category = categoryMapper.mapCategoryDtoToEntity(categoryDto);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public  Category updateCategory(CategoryDto categoryDto, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                // 如果找不到categoryId對應的資料，建立並拋出一個自己定義的例外
                .orElseThrow(()-> new ResourceNotFoundEx("Category not found with Id :"+categoryDto.getId()));

        // 不是單純Mapper，還有檢查是否有資料
        categoryMapper.updateCategoryFromDto(categoryDto, category);
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
