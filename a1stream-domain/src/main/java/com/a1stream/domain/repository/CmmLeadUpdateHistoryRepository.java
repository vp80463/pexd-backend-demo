package com.a1stream.domain.repository;

import com.a1stream.domain.custom.CmmLeadUpdateHistoryRepositoryCustom;
import com.a1stream.domain.entity.CmmLeadUpdateHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CmmLeadUpdateHistoryRepository extends JpaExtensionRepository<CmmLeadUpdateHistory, Long>, CmmLeadUpdateHistoryRepositoryCustom {

}
