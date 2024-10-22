package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSpecialClaimSerialProRepositoryCustom;
import com.a1stream.domain.entity.CmmSpecialClaimSerialPro;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSpecialClaimSerialProRepository extends JpaExtensionRepository<CmmSpecialClaimSerialPro, Long>, CmmSpecialClaimSerialProRepositoryCustom {

    CmmSpecialClaimSerialPro findFirstBySpecialClaimIdAndSerializedProductId(Long specialClaimId, Long cmmSerializedProId);

    List<CmmSpecialClaimSerialPro> findBySpecialClaimIdInAndSerializedProductId(List<Long> specialClaimIdList, Long cmmSerializedProId);

     @Query(value="select *                                "
                + "from cmm_special_claim_serial_pro cscsp "
                + "where dealer_cd =:siteId                "
                + "and facility_cd =:facilityCd            "
                + "and claim_flag  =:claimFlag             "
                + "and frame_no   in (:frameNos)           "
                + "and special_claim_id in (:claimIds)     ", nativeQuery=true)
     List<CmmSpecialClaimSerialPro> getByClaimIds(@Param("siteId") String siteId
                                                , @Param("facilityCd") String facilityCd
                                                , @Param("claimFlag") String claimFlag
                                                , @Param("frameNos") Set<String> frameNos
                                                , @Param("claimIds") Set<Long> claimIds);

     @Query(value="select *                                "
             + "from cmm_special_claim_serial_pro cscsp "
             + "where frame_no   =:frameNo                "
             + "and special_claim_id in (:claimIds)     ", nativeQuery=true)
     List<CmmSpecialClaimSerialPro> getSpecialClaimSerialProdDetail(@Param("frameNo") String frameNo, @Param("claimIds") Set<Long> claimIds);

     @Query(value="select *                                "
             + "from cmm_special_claim_serial_pro cscsp "
             + "where special_claim_id in (:claimIds)     ", nativeQuery=true)
     List<CmmSpecialClaimSerialPro> findSpecClaimSerialProdDtl(@Param("claimIds") Set<Long> claimIds);


     @Modifying
     @Query("DELETE FROM CmmSpecialClaimSerialPro e WHERE e.specialClaimSerialProId in :deleteIds")
     void removeSpecialClaimSerialProByKey(@Param("deleteIds") Set<Long> deleteIds);
}
