package com.example.MutsaMarket.service;

import com.example.MutsaMarket.dto.ResponseDto;
import com.example.MutsaMarket.dto.SalesItemDto;
import com.example.MutsaMarket.entity.SalesItemEntity;
import com.example.MutsaMarket.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesItemService {
    private final SalesItemRepository salesItemRepository;

    public ResponseDto createSalesItem(SalesItemDto salesItemDto) {

        SalesItemEntity salesItemEntity = new SalesItemEntity();
        salesItemEntity.setTitle(salesItemDto.getTitle());
        salesItemEntity.setDescription(salesItemDto.getDescription());
        salesItemEntity.setMinPriceWanted(salesItemDto.getMinPriceWanted());
        salesItemEntity.setStatus("판매중");
        salesItemEntity.setWriter(salesItemDto.getWriter());
        salesItemEntity.setPassword(salesItemDto.getPassword());
        salesItemRepository.save(salesItemEntity);

        return new ResponseDto("등록이 완료되었습니다.");
    }

    public SalesItemDto readSalesItem(Long id){
        Optional<SalesItemEntity> optionalSale = salesItemRepository.findById(id);
        if(optionalSale.isPresent())
            return SalesItemDto.fromEntity(optionalSale.get());
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public Page<SalesItemDto> readSalesItemAll(Integer page, Integer limit){
        Pageable pageable = PageRequest.of(page, limit);
        Page<SalesItemEntity> salesItemEntities = salesItemRepository.findAll(pageable);
            Page<SalesItemDto> salesItemDtos = salesItemEntities.map(SalesItemDto::fromEntity);
        return salesItemDtos;
    }

    //반복되어 사용되는 getItemByID를 따로 빼놓는다.
    public SalesItemEntity getItemById(Long id) {
        Optional<SalesItemEntity> optionalSalesItemEntity = salesItemRepository.findById(id);
        if (optionalSalesItemEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalSalesItemEntity.get();
    }
    //패스워드가 일치하는지 확인
    public void validatePassword(SalesItemEntity salesItemEntity, String password) {
        if (!password.equals(salesItemEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    //물품 수정
    public ResponseDto updateSalesItem(Long id, SalesItemDto salesItemDto) {
        SalesItemEntity salesItemEntity = getItemById(id);
        validatePassword(salesItemEntity, salesItemDto.getPassword());

        salesItemEntity.setTitle(salesItemDto.getTitle());
        salesItemEntity.setDescription(salesItemDto.getDescription());
        salesItemEntity.setMinPriceWanted(salesItemDto.getMinPriceWanted());
        salesItemEntity.setWriter(salesItemDto.getWriter());
        salesItemEntity.setPassword(salesItemDto.getPassword());
        salesItemRepository.save(salesItemEntity);
        return new ResponseDto("물품이 업데이트 되었습니다.");
    }
    //이미지 업로드
    public ResponseDto updateSalesItemWithImage(Long id, MultipartFile image, String password) {

        SalesItemEntity salesItemEntity = getItemById(id);
        validatePassword(salesItemEntity, password);

        salesItemEntity.setImageUrl(saveImage(id,image));
        salesItemRepository.save(salesItemEntity);
        return new ResponseDto("이미지가 등록되었습니다.");
    }
    //이미지 저장
    public String saveImage(Long id, MultipartFile image) {
        //폴더를 만드는 과정
        String salesItemImageDir = String.format("images/%d",id);
        try {
            Files.createDirectories(Path.of(salesItemImageDir));
        }catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //확장자 포함한 이미지 만들기
        String originalFilename = image.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        UUID random = UUID.randomUUID();
        String newFilename = random.toString() + "." + extension;

        //폴더와 파일 경로를 포함한 이름 만들기
        String imagePath = salesItemImageDir + newFilename;
        //저장하기
        try {
            image.transferTo(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return String.format("/static/%s", newFilename);

    }
    //물품 삭제
    public ResponseDto deleteSalesItem(Long id, SalesItemDto salesItemDto){
        SalesItemEntity salesItemEntity = getItemById(id);
        validatePassword(salesItemEntity,salesItemDto.getPassword());
        salesItemRepository.deleteById(salesItemEntity.getId());
        return new ResponseDto("물품이 삭제되었습니다.");
        }
}
