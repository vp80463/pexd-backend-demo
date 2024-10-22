package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.PoCancelHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PoCancelHistoryRepository extends JpaExtensionRepository<PoCancelHistory, Long> {

}
