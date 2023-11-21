package com.example.MutsaMarket.dto;

import com.example.MutsaMarket.entity.NegotiationEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NegotiationDto {
    private Long id;
    @NotNull(message = "물품이 존재하지 않습니다.")
    private Long itemId;
    @NotNull(message = "제안 가격을 작성해주세요.")
    private Long suggestedPrice;
    private String status;
    @NotNull(message = "작성자를 입력해주세요.")
    private String writer;
    private String password;

    public static NegotiationDto fromEntity(NegotiationEntity negotiationEntity) {
        NegotiationDto negotiationDto = new NegotiationDto();
        negotiationDto.setId(negotiationEntity.getId());
        negotiationDto.setItemId(negotiationEntity.getItemId());
        negotiationDto.setSuggestedPrice(negotiationEntity.getSuggestedPrice());
        negotiationDto.setStatus(negotiationEntity.getStatus());
        negotiationDto.setWriter(negotiationEntity.getWriter());
        return negotiationDto;
    }
}