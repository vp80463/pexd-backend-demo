package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSpecialClaimRepairRepositoryCustom;
import com.a1stream.domain.entity.CmmSpecialClaimRepair;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSpecialClaimRepairRepository extends JpaExtensionRepository<CmmSpecialClaimRepair, Long>, CmmSpecialClaimRepairRepositoryCustom {

    List<CmmSpecialClaimRepair> findBySpecialClaimId(Long specialClaimId);

    @Modifying
    @Query("DELETE FROM CmmSpecialClaimRepair e WHERE e.specialClaimId in :deleteIds")
    void removeRelatedRepair(@Param("deleteIds") Set<Long> deleteIds);
}
