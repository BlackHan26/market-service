package com.example.MutsaMarket.dto;

import com.example.MutsaMarket.entity.SalesItemEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesItemDto {
    private Long id;
    @NotNull(message = "제목을 작성해주세요.")
    private String title;
    @NotNull(message = "설명을 작성해주세요.")
    private String description;
    private String imageURL;
    @NotNull(message = "최소 가격을 작성해주세요.")
    private Long minPriceWanted;
    private String status;
    @NotNull(message = "작성자를 작성해주세요.")
    private String writer;
    private String password;

    public static SalesItemDto fromEntity(SalesItemEntity entity) {
        SalesItemDto salesItemDto = new SalesItemDto();
        salesItemDto.setId(entity.getId());
        salesItemDto.setTitle(entity.getTitle());
        salesItemDto.setDescription(entity.getDescription());
        salesItemDto.setImageURL(entity.getImageUrl());
        salesItemDto.setMinPriceWanted(entity.getMinPriceWanted());
        salesItemDto.setStatus(entity.getStatus());
        salesItemDto.setWriter(entity.getWriter());
        salesItemDto.setPassword(entity.getPassword());
        return salesItemDto;
    }
}