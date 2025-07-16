package com.sideproject.myshop.mapper;

import com.sideproject.myshop.dto.CategoryDto;
import com.sideproject.myshop.dto.CategoryTypeDto;
import com.sideproject.myshop.entities.Category;
import com.sideproject.myshop.entities.CategoryType;
import com.sideproject.myshop.exceptions.ResourceNotFoundEx;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {


    public Category mapCategoryDtoToEntity(CategoryDto categoryDto){
        Category category = Category.builder()
                .code(categoryDto.getCode())
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();
        if(categoryDto.getCategoryTypes() != null){
            List<CategoryType> categoryTypes = mapCategoryTypeDtosToEntity(categoryDto.getCategoryTypes(), category);
            category.setCategoryTypes(categoryTypes);
        }
        return category;
    }


    public List<CategoryType> mapCategoryTypeDtosToEntity(List<CategoryTypeDto> categoryTypeList, Category category) {
        return categoryTypeList.stream()
                .map(categoryTypeDto -> {
                    CategoryType categoryType = new CategoryType();
                    categoryType.setCode(categoryTypeDto.getCode());
                    categoryType.setName(categoryTypeDto.getName());
                    categoryType.setDescription(categoryTypeDto.getDescription());
                    categoryType.setCategory(category);
                    return categoryType;
                })
                .collect(Collectors.toList());
    }

    public void updateCategoryFromDto(CategoryDto categoryDto, Category category) {
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        if (categoryDto.getCode() != null) {
            category.setCode(categoryDto.getCode());
        }
        if (categoryDto.getDescription() != null) {
            category.setDescription(categoryDto.getDescription());
        }

        if (categoryDto.getCategoryTypes() != null) {
            List<CategoryType> updatedList = new ArrayList<>();
            List<CategoryType> existing = category.getCategoryTypes();

            // 從 categoryDto.getCategoryTypes() 裡一筆一筆拿出來，暫時叫做 typeDto，然後對每一筆做處理
            for (CategoryTypeDto typeDto : categoryDto.getCategoryTypes()) {
                if (typeDto.getId() != null) {

                    // 更新已有 CategoryType
                    CategoryType categoryType = existing.stream()
                            .filter(t -> t.getId().equals(typeDto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundEx("CategoryType not found with id: " + typeDto.getId()));

                    categoryType.setName(typeDto.getName());
                    categoryType.setCode(typeDto.getCode());
                    categoryType.setDescription(typeDto.getDescription());
                    updatedList.add(categoryType);
                } else {
                    // 新增新的 CategoryType
                    CategoryType newType = new CategoryType();
                    newType.setName(typeDto.getName());
                    newType.setCode(typeDto.getCode());
                    newType.setDescription(typeDto.getDescription());
                    newType.setCategory(category);
                    updatedList.add(newType);
                }
            }
            category.setCategoryTypes(updatedList);
        }
    }

}
