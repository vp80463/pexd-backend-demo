package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmServiceHistoryItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmServiceHistoryItemRepository extends JpaExtensionRepository<CmmServiceHistoryItem, Long> {

}
