package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmServiceJobForDORepositoryCustom;
import com.a1stream.domain.entity.CmmServiceJobForDO;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;



@Repository
public interface CmmServiceJobForDORepository extends JpaExtensionRepository<CmmServiceJobForDO, Long>, CmmServiceJobForDORepositoryCustom {

    List<CmmServiceJobForDO> findByServiceJobForDoIdIn(List<Long> doIdList);
}
