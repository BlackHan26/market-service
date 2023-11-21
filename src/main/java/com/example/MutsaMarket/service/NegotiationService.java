package com.example.MutsaMarket.service;

import com.example.MutsaMarket.dto.NegotiationDto;
import com.example.MutsaMarket.dto.ResponseDto;
import com.example.MutsaMarket.dto.SalesItemDto;
import com.example.MutsaMarket.entity.CommentEntity;
import com.example.MutsaMarket.entity.NegotiationEntity;
import com.example.MutsaMarket.entity.SalesItemEntity;
import com.example.MutsaMarket.repository.NegotiationRepository;
import com.example.MutsaMarket.repository.SalesItemRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.Writer;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NegotiationService {
    private final NegotiationRepository negotiationRepository;
    private final SalesItemRepository salesItemRepository;
    private final SalesItemService salesItemService;

    //댓글이 존재하는 지 확인하는 메소드
    public NegotiationEntity getItemById(Long id) {
        //itemId를 ID로 가진 Entity가 존재하는지?
        Optional<NegotiationEntity> optionalNegotiationEntity = negotiationRepository.findById(id);
        if (optionalNegotiationEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalNegotiationEntity.get();
    }

    //패스워드가 일치하는지 확인하는 메소드
    public void validatePassword(NegotiationEntity negotiationEntity, String password) {
        if (!password.equals(negotiationEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    //제안을 한다.
    public ResponseDto createNego(Long itemId, NegotiationDto dto) {
        NegotiationEntity entity = new NegotiationEntity();
        entity.setItemId(itemId);
        entity.setSuggestedPrice(dto.getSuggestedPrice());
        entity.setStatus("제안");
        entity.setWriter(dto.getWriter());
        entity.setPassword(dto.getPassword());
        negotiationRepository.save(entity);
        return new ResponseDto("구매 제안이 등록되었습니다.");
    }

    //조회를 한다.
    public Page<NegotiationDto> readNego(Long itemId, String writer, String password, Integer page, Integer limit) {
        //물품이 있는지 확인
        getItemById(itemId);
        Pageable pageable = PageRequest.of(page, limit);

        // 등록자라면 제안을 모두 공개
        if (writer.equals(getItemById(itemId).getWriter())
                && password.equals(getItemById(itemId).getPassword())) {
            Page<NegotiationEntity> negotiationEntities = negotiationRepository.findAll(pageable);
            Page<NegotiationDto> negotiationDtos = negotiationEntities.map(NegotiationDto::fromEntity);
            return negotiationDtos;
        }
        //제안자라면 그 제안만 공개
        Page<NegotiationEntity> negotiationEntities = negotiationRepository.findByItemIdAndWriterAndPassword(itemId, writer, password, pageable);
        //제안자가 아니라면 오류
        if (negotiationEntities == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //맞다면
        Page<NegotiationDto> negotiationDtos = negotiationEntities.map(NegotiationDto::fromEntity);
        return negotiationDtos;
    }

    //제안 수정
    public ResponseDto updateNego(Long itemId, Long proposalId, NegotiationDto negotiationDto) {
        getItemById(itemId);
        NegotiationEntity negotiationEntity = getItemById(proposalId);
        // entity의 작성자와 dto의 작성자가 일치하는지
        if (!negotiationDto.getWriter().equals(negotiationEntity.getWriter()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //맞다면 진행
        validatePassword(negotiationEntity, negotiationDto.getPassword());
        negotiationEntity.setSuggestedPrice(negotiationDto.getSuggestedPrice());
        negotiationRepository.save(negotiationEntity);

        return new ResponseDto("제안이 수정되었습니다.");
    }

    public ResponseDto deleteNego(Long itemId, Long proposalId, NegotiationDto negotiationDto) {

        NegotiationEntity negotiationEntity = getItemById(proposalId);
        // entity의 작성자와 dto의 작성자가 일치하는지
        if (!negotiationDto.getWriter().equals(negotiationEntity.getWriter()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //맞다면 진행
        validatePassword(negotiationEntity, negotiationDto.getPassword());
        negotiationRepository.deleteById(negotiationEntity.getId());
        return new ResponseDto("제안을 삭제했습니다.");
    }

    public ResponseDto updateNegoAcceptedOrRefused(Long itemId, Long proposalId, NegotiationDto negotiationDto) {
        //존재 확인
        SalesItemEntity salesItemEntity = salesItemService.getItemById(itemId);
        NegotiationEntity negotiationEntity = getItemById(proposalId);

        // entity의 작성자와 dto의 작성자가 일치하는지
        // 구매 제안자일 때
        if (negotiationDto.getWriter().equals(negotiationEntity.getWriter())) {
            //비밀번호 맞는지 체크
            validatePassword(negotiationEntity, negotiationDto.getPassword());
            //맞다면 진행
            if (negotiationEntity.getStatus().equals("제안")
                    && negotiationDto.getSuggestedPrice() != null) {
                updateNego(itemId, proposalId, negotiationDto);
                return new ResponseDto("제안이 수정되었습니다.");
            }

            if (negotiationEntity.getStatus().equals("수락")
                    && negotiationDto.getStatus().equals("확정")) {
                salesItemEntity.setStatus("판매 완료");
                SalesItemDto.fromEntity(salesItemRepository.save(salesItemEntity));

                //제안이 확정되지 않은 모든 제안에대해서는 거절.
                List<NegotiationEntity> negotiationEntityList = negotiationRepository.findAll();
                for (NegotiationEntity negotiationEntites : negotiationEntityList) {
                    negotiationEntites.setStatus("거절");
                    negotiationRepository.save(negotiationEntites);
                }
                negotiationEntity.setStatus("확정");
                negotiationRepository.save(negotiationEntity);
                return new ResponseDto("구매가 확정되었습니다");
            }
        }
        // 등록 작성자일 때
        if (negotiationDto.getWriter().equals(salesItemEntity.getWriter())) {
            validatePassword(negotiationEntity, negotiationDto.getPassword());
            negotiationEntity.setStatus(negotiationDto.getStatus());
            NegotiationDto.fromEntity(negotiationRepository.save(negotiationEntity));
            return new ResponseDto("제안의 상태가 변경되었습니다.");
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}