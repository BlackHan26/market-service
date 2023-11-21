package com.example.MutsaMarket.Controller;

import com.example.MutsaMarket.dto.ResponseDto;
import com.example.MutsaMarket.dto.SalesItemDto;
import com.example.MutsaMarket.service.SalesItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class SalesItemController {

    private final SalesItemService service;

    @PostMapping
    public ResponseDto create(@RequestBody SalesItemDto salesItemDto) {
        return service.createSalesItem(salesItemDto);
    }

    @GetMapping("/{id}")
    public SalesItemDto read(@PathVariable("id") Long id) {
        return service.readSalesItem(id);
    }

    @GetMapping
    public Page<SalesItemDto> readAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        return service.readSalesItemAll(page, limit);
    }
    //수정
    @PutMapping("/{id}")
    public ResponseDto update(
            @PathVariable("id") Long id,
            @RequestBody SalesItemDto salesItemDto) {
        return service.updateSalesItem(id, salesItemDto);
    }
    @PutMapping("/{id}/image")
    public ResponseDto updateWithImage(
            @PathVariable("id") Long id,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "password") String password) {
        return service.updateSalesItemWithImage(id, image, password);
    }
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable("id") Long id, @RequestBody SalesItemDto salesItemDto) {
        return service.deleteSalesItem(id, salesItemDto);
    }
}