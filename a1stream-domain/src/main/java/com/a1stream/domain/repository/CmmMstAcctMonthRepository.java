package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmMstAcctMonth;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmMstAcctMonthRepository extends JpaExtensionRepository<CmmMstAcctMonth, Long> {

}
