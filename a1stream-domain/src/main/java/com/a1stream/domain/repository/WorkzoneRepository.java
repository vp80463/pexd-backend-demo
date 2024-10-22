package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.WorkzoneRepositoryCustom;
import com.a1stream.domain.entity.Workzone;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface WorkzoneRepository extends JpaExtensionRepository<Workzone, Long>,WorkzoneRepositoryCustom  {

    List<Workzone> findBySiteId(String siteId);

    List<Workzone> findBySiteIdAndFacilityId(String siteId, Long facilityId);

    @Query(value="select * from workzone              "
                + "where site_id =:siteId             "
                + "  and (workzone_cd = :workZoneCd   "
                + "   or description = :description ) ", nativeQuery=true)
    List<Workzone> getWorkZoneByCd(@Param("siteId") String siteId
                                 , @Param("workZoneCd") String workZoneCd
                                 , @Param("description") String workzoneNm);
    @Query(value="select * from workzone              "
                + "where site_id =:siteId             "
                + "  and description = :description   ", nativeQuery=true)
    List<Workzone> getWorkZoneByNm(@Param("siteId") String siteId
                                 , @Param("description") String workzoneNm);

    List<Workzone> findByWorkzoneIdIn(Set<Long> workzoneIds);

    List<Workzone> findBySiteIdAndFacilityIdAndWorkzoneCdIn(String siteId,Long facilityId, Set<String> workzoneCds);

    Workzone findFirstByFacilityIdAndWorkzoneId(Long facilityId, Long workzoneId);
}
