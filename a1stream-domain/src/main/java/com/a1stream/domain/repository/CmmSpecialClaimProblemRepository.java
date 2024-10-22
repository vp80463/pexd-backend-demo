package com.a1stream.domain.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSpecialClaimProblemRepositoryCustom;
import com.a1stream.domain.entity.CmmSpecialClaimProblem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface CmmSpecialClaimProblemRepository extends JpaExtensionRepository<CmmSpecialClaimProblem, Long>, CmmSpecialClaimProblemRepositoryCustom {

    @Modifying
    @Query("DELETE FROM CmmSpecialClaimProblem e WHERE e.specialClaimId in :deleteIds")
    void removeRelatedProblem(@Param("deleteIds") Set<Long> deleteIds);
}
