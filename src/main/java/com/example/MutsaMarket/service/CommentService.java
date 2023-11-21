package com.example.MutsaMarket.service;

import com.example.MutsaMarket.dto.CommentDto;
import com.example.MutsaMarket.dto.ResponseDto;
import com.example.MutsaMarket.entity.CommentEntity;
import com.example.MutsaMarket.entity.SalesItemEntity;
import com.example.MutsaMarket.repository.CommentRepository;
import com.example.MutsaMarket.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor //생성자를 만들고 Spring이 의존성 주입까지 해준다.
public class CommentService {
    private final CommentRepository commentRepository;
    private final SalesItemRepository salesItemRepository;

    //댓글이 존재하는 지 확인하는 메소드
    public CommentEntity getItemById(Long id) {
        //itemId를 ID로 가진 Entity가 존재하는지?
        Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(id);
        if (optionalCommentEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalCommentEntity.get();
    }
    //패스워드가 일치하는지 확인하는 메소드
    public void validatePassword(CommentEntity commentEntity, String password) {
        if (!password.equals(commentEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public ResponseDto createComment(Long itemId, CommentDto commentDto) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setItemId(itemId);
        commentEntity.setWriter(commentDto.getWriter());
        commentEntity.setPassword(commentDto.getPassword());
        commentEntity.setContent(commentDto.getContent());
        commentRepository.save(commentEntity);
        return new ResponseDto("댓글이 등록되었습니다");
    }
    //댓글 조회
    public Page<CommentDto> readCommentAll(Long itemId, Integer page, Integer limit ) {
        getItemById(itemId);
        Pageable pageable = PageRequest.of(page, limit);
        Page<CommentEntity> commentEntities = commentRepository.findAll(pageable);
        Page<CommentDto> commentDtos = commentEntities.map(CommentDto::fromEntity);
        return commentDtos;
    }
    //댓글 수정
    public ResponseDto updateComment(Long itemId, Long commentId, CommentDto commentDto) {
        CommentEntity commentEntity = getItemById(commentId);
        validatePassword(commentEntity, commentDto.getPassword());
       //대상 댓글이 대상게시글의 댓글이 맞는지
        if (!itemId.equals(commentEntity.getItemId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //맞다면 진행
        commentEntity.setContent(commentDto.getContent());
        commentRepository.save(commentEntity);
        return new ResponseDto("댓글이 수정되었습니다.");
    }
    //댓글 삭제
    public ResponseDto deleteComment(Long itemId, Long commentId, CommentDto commentDto) {
        CommentEntity commentEntity = getItemById(commentId);
        validatePassword(commentEntity, commentDto.getPassword());
        //대상 댓글이 대상게시글의 댓글이 맞는지
        if (!itemId.equals(commentEntity.getItemId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //맞다면 진행
        commentRepository.deleteById(commentEntity.getId());
        return new ResponseDto("댓글을 삭제했습니다.");
    }
    //답글 달기
    public ResponseDto addReply(Long itemId, Long commentId, CommentDto dto) {
        //댓글이 있는지 확인
        CommentEntity commentEntity = getItemById(commentId);
        //대상 댓글이 대상게시글의 댓글이 맞는지
        if (!itemId.equals(commentEntity.getItemId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //맞다면 진행
        //물품 등록자인지 확인
        Optional<SalesItemEntity> checkItem = salesItemRepository.findById(commentEntity.getItemId());
        if (!dto.getPassword().equals(checkItem.get().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //맞다면 진행한다.
        commentEntity.setReply(dto.getReply());
        commentRepository.save(commentEntity);

        return new ResponseDto("답변이 추가되었습니다.");
    }

}