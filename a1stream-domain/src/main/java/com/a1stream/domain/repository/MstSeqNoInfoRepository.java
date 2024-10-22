package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstSeqNoInfoRepositoryCustom;
import com.a1stream.domain.entity.MstSeqNoInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstSeqNoInfoRepository extends JpaExtensionRepository<MstSeqNoInfo, Long>, MstSeqNoInfoRepositoryCustom {

//    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
//    @Query("  FROM MstSeqNoInfo                         "
//            + " WHERE seqNoType = ?1                       "
//            + "   AND (TRIM(?2) = '' OR siteId       = ?2) "
//            + "   AND (?3 is null OR facilityId   = ?3) "
//            + " ")
//    MstSeqNoInfo getBySeqNoType(String seqNoType, String siteId, Long facilityId);
}
