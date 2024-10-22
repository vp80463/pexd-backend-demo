package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmLeadManagementUnitRepositoryCustom;
import com.a1stream.domain.entity.CmmLeadManagementUnit;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmLeadManagementUnitRepository extends JpaExtensionRepository<CmmLeadManagementUnit, Long>, CmmLeadManagementUnitRepositoryCustom {

    List<CmmLeadManagementUnit> findByLeadManagementResultIdIn(List<Long> leadManagementResultIds);

}
