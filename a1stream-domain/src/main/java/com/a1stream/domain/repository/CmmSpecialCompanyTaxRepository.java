package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSpecialCompanyTaxRepositoryCustom;
import com.a1stream.domain.entity.CmmSpecialCompanyTax;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSpecialCompanyTaxRepository extends JpaExtensionRepository<CmmSpecialCompanyTax, Long>, CmmSpecialCompanyTaxRepositoryCustom {

    List<CmmSpecialCompanyTax> findBySpecialCompanyTaxIdIn(Set<Long> specialCompanyTaxIds);

    List<CmmSpecialCompanyTax> findBySpecialCompanyTaxCdIn(Set<String> specialCompanyTaxCds);

    CmmSpecialCompanyTax findBySpecialCompanyTaxCd(String specialCompanyTaxCd);

    CmmSpecialCompanyTax findBySpecialCompanyTaxCdAndSpecialCompanyTaxNmAndAddress(String specialCompanyTaxCd, String specialCompanyTaxNm, String address);
}
