package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.PartsRopqMonthly;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PartsRopqMonthlyRepository extends JpaExtensionRepository<PartsRopqMonthly, Long> {

    List<PartsRopqMonthly> findBySiteIdAndProductIdInAndRopqTypeIn(String siteId,Set<Long> productId, Set<String> ropqType);

    List<PartsRopqMonthly> findBySiteIdAndProductIdInAndRopqType(String siteId, List<Long> productIdList, String ropqType);
}
