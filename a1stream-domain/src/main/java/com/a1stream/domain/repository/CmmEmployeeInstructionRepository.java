package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmEmployeeInstructionRepositoryCustom;
import com.a1stream.domain.entity.CmmEmployeeInstruction;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmEmployeeInstructionRepository extends JpaExtensionRepository<CmmEmployeeInstruction, Long>, CmmEmployeeInstructionRepositoryCustom {

    List<CmmEmployeeInstruction> findByCmmEmployeeInstructionIdIn(List<Long> cmmEmployeeInstructionIdList);

    CmmEmployeeInstruction findByCmmEmployeeInstructionId(Long cmmEmployeeInstructionId);
}
