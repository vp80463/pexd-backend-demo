package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmSerializedProductTran;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSerializedProductTranRepository extends JpaExtensionRepository<CmmSerializedProductTran, Long> {

}
