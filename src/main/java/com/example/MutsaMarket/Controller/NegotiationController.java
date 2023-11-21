package com.example.MutsaMarket.Controller;

import com.example.MutsaMarket.dto.NegotiationDto;
import com.example.MutsaMarket.dto.ResponseDto;
import com.example.MutsaMarket.service.NegotiationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items/{itemId}/proposals")
public class NegotiationController {
    private final NegotiationService service;

    @PostMapping
    public ResponseDto create(
            @PathVariable("itemId") Long itemId,
            @RequestBody NegotiationDto negotiationDto) {
        return service.createNego(itemId, negotiationDto);
    }
    //조회
    @GetMapping
    public Page<NegotiationDto> readNego(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "writer") String writer,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return service.readNego(itemId, writer, password, page, limit);
    }
    //수정

    //삭제
    @DeleteMapping("/{proposalId}")
    public ResponseDto delete(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long proposalId,
            @RequestBody NegotiationDto negotiationDto) {
        return service.deleteNego(itemId, proposalId, negotiationDto);
    }
    @PutMapping("/{proposalId}")
    public ResponseDto updateProposal(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long proposalId,
            @RequestBody NegotiationDto negotiationDto) {
        return service.updateNegoAcceptedOrRefused(proposalId, itemId, negotiationDto);
    }
}
