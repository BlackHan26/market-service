package com.example.MutsaMarket.dto;

import com.example.MutsaMarket.entity.CommentEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

    @Data
    public class CommentDto {
        private Long id;
        @NotNull(message = "물품이 존재하지 않습니다.")
        private Long itemId;
        @NotNull(message = "작성자를 입력해주세요.")
        private String writer;
        private String password;
        @NotNull(message = "댓글을 입력해주세요.")
        private String content;
        private String reply;

        public static CommentDto fromEntity(CommentEntity commentEntity) {
            CommentDto commentDto = new CommentDto();
            commentDto.setId(commentEntity.getId());
            commentDto.setItemId(commentEntity.getItemId());
            commentDto.setWriter(commentEntity.getWriter());
            commentDto.setPassword(commentEntity.getPassword());
            commentDto.setContent(commentEntity.getContent());
            commentDto.setReply(commentEntity.getReply());
            return commentDto;
        }
    }