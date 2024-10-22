package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmPdiRepositoryCustom;
import com.a1stream.domain.entity.CmmPdi;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmPdiRepository extends JpaExtensionRepository<CmmPdi, Long>, CmmPdiRepositoryCustom {

}
