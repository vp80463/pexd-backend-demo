package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstSeqNoSettingRepositoryCustom;
import com.a1stream.domain.entity.MstSeqNoSetting;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface MstSeqNoSettingRepository extends JpaExtensionRepository<MstSeqNoSetting, Long>, MstSeqNoSettingRepositoryCustom {

//	@Query("  FROM MstSeqNoSetting                         "
//            + " WHERE seqNoType = ?1                       "
//            + "   AND (TRIM(?2) = '' OR TRIM(siteId) = '' OR siteId       = ?2) "
//            + "   AND (?3 is null OR facilityId is null OR facilityId   = ?3) "
//            + "   ORDER BY siteId, facilityId "
//            + " ")
//	List<MstSeqNoSetting> getBySeqNoType(String seqNoType, String siteId, Long facilityId);
}
