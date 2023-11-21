package com.example.MutsaMarket.repository;

import com.example.MutsaMarket.entity.SalesItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItemEntity, Long> {
}