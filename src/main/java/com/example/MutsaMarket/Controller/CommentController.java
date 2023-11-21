package com.example.MutsaMarket.Controller;

import com.example.MutsaMarket.dto.CommentDto;
import com.example.MutsaMarket.dto.ResponseDto;
import com.example.MutsaMarket.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items/{itemId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;
    @PostMapping
    public ResponseDto create(@PathVariable("itemId") Long itemId, @RequestBody CommentDto commentDto){
        return service.createComment(itemId, commentDto);
    }
    @GetMapping
    public Page<CommentDto> readAll(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return service.readCommentAll(itemId, page, limit);
    }
    //수정
    @PutMapping("/{commentId}")
    public ResponseDto update(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto commentDto) {
        return service.updateComment(itemId, commentId, commentDto);
    }

    //삭제
    @DeleteMapping("/{commentId}")
    public ResponseDto delete(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto commentDto) {
        return service.deleteComment(itemId, commentId, commentDto);
    }
    //답글 달기
    @PutMapping("/{commentId}/reply")
    public ResponseDto addReply(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto commentDto) {
        return service.addReply(itemId, commentId, commentDto);
    }
}
