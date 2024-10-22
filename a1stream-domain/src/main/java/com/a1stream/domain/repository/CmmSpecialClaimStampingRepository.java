package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmSpecialClaimStamping;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;



@Repository
public interface CmmSpecialClaimStampingRepository extends JpaExtensionRepository<CmmSpecialClaimStamping, Long> {

    List<CmmSpecialClaimStamping> findBySiteIdAndSpecialClaimId(String siteId, Long specialClaimId);

    @Modifying
    @Query("DELETE FROM CmmSpecialClaimStamping e WHERE e.specialClaimId in :deleteIds")
    void removeRelatedStamp(@Param("deleteIds") Set<Long> deleteIds);
}
