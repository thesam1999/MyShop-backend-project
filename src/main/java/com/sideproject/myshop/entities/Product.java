package com.sideproject.myshop.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue //資料庫會自動生成符合UUID的主鍵，通常是自增
    //當類型是UUID時候，不會依賴資料庫的自增，會先在應用層生成
    private UUID id; //UUID比int當主鍵安全，但需要程式碼生成id

    @Column(nullable = false) //不允許為null
    private String name;

//    @Column  預設可以不加上！
    private String description;

    @Column(nullable = false)
    private BigDecimal price; //比一般(float or double)還要精準，沒有誤差

    @Column(nullable = false)
    private String brand;

    private Float rating;

    @Column(nullable = false)
    private boolean isNewArrival;

    @Column(nullable = false,unique = true)
    private String slug;

//  舊的用法，現在不推薦使用，因為無法處理時區問題，且對開發者不夠直觀
//    @Column(nullable = false, updatable = false)
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdAt;
//    @Column(nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date updatedAt;

    @Column(nullable = false, updatable = false)
    // updatable = false：這個欄位一旦設置（例如，資料首次插入時），就不能再被更新。這確保了 createdAt 一旦設置，就不會隨著資料更新而改變。
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<ProductVariant> productVariants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    @JsonIgnore //避免循環引用
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY) //通常是這一端維護外建。Lazy是hibernate僅會載入外件值，帶真正訪問該關聯屬性才會執行SQL語法查整個物件(常用作法)
    @JoinColumn(name = "categoryType_id",nullable = false)
    @JsonIgnore //在輸出的 JSON 中不會顯示，並且在從 JSON 解析回 Java 物件時，這個欄位會被忽略。
    private CategoryType categoryType;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL) //操作product時候，resource會跟著一起被操作
    private List<Resources> resources;

//搭配 @Temporal過時的用法
//    @PrePersist //在物件第一次被存入資料庫之前要執行的方法
//    protected void onCreate() {
//        createdAt = new Date();
//        updatedAt = createdAt;
//    }
//
//    @PreUpdate //物件每次被更新（但不是新增）之前」執行的方法
//    protected void onUpdate() {
//        updatedAt = new java.util.Date();
//    }
}
