package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmConditionRepositoryCustom;
import com.a1stream.domain.entity.CmmCondition;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmConditionRepository extends JpaExtensionRepository<CmmCondition, Long>,CmmConditionRepositoryCustom {

    CmmCondition findFirstByConditionCd(String conditionCd);
}
