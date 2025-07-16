package com.sideproject.myshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//儲存產品相關的資訊，像是類型、圖片...等等
public class Resources {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    //是否為商品的Primary image主圖
    private Boolean isPrimary;

    @Column(nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}
