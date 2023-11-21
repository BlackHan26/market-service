package com.example.MutsaMarket.repository;

import com.example.MutsaMarket.entity.NegotiationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegotiationRepository extends JpaRepository<NegotiationEntity, Long> {
    Page<NegotiationEntity> findByItemIdAndWriterAndPassword(Long itemId, String writer, String password, Pageable pageable);
}
