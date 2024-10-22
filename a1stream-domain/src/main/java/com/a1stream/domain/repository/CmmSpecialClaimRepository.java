package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSpecialClaimRepositoryCustom;
import com.a1stream.domain.entity.CmmSpecialClaim;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSpecialClaimRepository extends JpaExtensionRepository<CmmSpecialClaim, Long>, CmmSpecialClaimRepositoryCustom{

    List<CmmSpecialClaim> findByBulletinNo(String bulletinNo);

    List<CmmSpecialClaim> findByBulletinNoIn(Set<String> bulletinNos);

    List<CmmSpecialClaim> findByCampaignNoIn(Set<String> campaignNos);
}
