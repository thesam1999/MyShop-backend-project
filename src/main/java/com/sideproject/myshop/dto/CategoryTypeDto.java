package com.sideproject.myshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryTypeDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
}
