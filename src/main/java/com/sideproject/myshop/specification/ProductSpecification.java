package com.sideproject.myshop.specification;

import com.sideproject.myshop.entities.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

//Specification 是 Spring Data JPA 提供的一個接口，建立動態查詢條件，並將這些條件應用於資料庫查詢。
//當你有多種查詢條件（可能來自前端過濾器）且這些條件是「可選的」
//設你有一個 `Product` 實體，包含：
//- `name`（商品名稱）
//- `price`（價格）
//- `brand`（品牌）
//你可能希望使用這些條件來查詢商品，但使用者可能只會給其中一兩個條件。
public class ProductSpecification {

    // Specification<>是一個Functional interface所以可以用lambda實現
    //設定成static讓他可以在不創造物件情況下使用

    // root 代表前查詢的是哪個實體，用來存取欄位。root.get("name") 表示 Product.name 欄位
    // query 代表整個查詢，可以設定排序、分組等 。query.distinct(true)
    // criteriaBuilder ，用來建立條件 等於、不等於、大於、小於、like 模糊查詢、in 子句等（like、equal、greaterThan )。criteriaBuilder.equal()

    // 查詢所有 Product 物件，並且只返回那些 category.id 等於 categoryId 的 Product 物件。
    public static Specification<Product> hasCategoryId(UUID categoryId){
        return  (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"),categoryId); // category對應另一個實體，所以再用一次get
    }

   // 查詢出所有 categoryType.id 等於 someTypeId 的 Product
    public static Specification<Product> hasCategoryTypeId(UUID typeId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("categoryType").get("id"),typeId);// categoryType對應另一個實體，所以再用一次get
    }
}
