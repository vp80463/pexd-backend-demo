package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstFacilityRepositoryCustom;
import com.a1stream.domain.entity.MstFacility;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstFacilityRepository extends JpaExtensionRepository<MstFacility, Long>, MstFacilityRepositoryCustom {

    MstFacility findByFacilityId(Long facilityId);

    List<MstFacility> findBySiteIdAndFacilityCdIn(String siteId, Set<String> facilityCd);

    List<MstFacility> findByFacilityIdIn(Set<Long> facilityIds);

    @Query(value="SELECT * FROM mst_facility "
            + "WHERE site_id IN :siteIdSet "
            + "AND facility_cd IN :facilityCdSet "
            + "AND shop_flag =:shopFlag "
            + "AND warehouse_flag =:warehouseFlag", nativeQuery=true)
    List<MstFacility> getMstFacility(@Param("siteIdSet") Set<String> siteIdSet
                                   , @Param("facilityCdSet") Set<String> facilityCdSet
                                   , @Param("shopFlag") String shopFlag
                                   , @Param("warehouseFlag") String warehouseFlag);

    List<MstFacility> findBySiteIdInAndFacilityCdIn(Set<String> siteIdSet, Set<String> facilityCdSet);

    MstFacility findBySiteIdAndFacilityCd(String siteId, String facilityCd);

    List<MstFacility> findBySiteId(String siteId);

    List<MstFacility> findBySiteIdInAndFacilityRoleTypeAndSpPurchaseFlag(Set<String> siteIdSet, String facilityRoleType, String spPurchaseFlag);
}
