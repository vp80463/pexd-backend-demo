package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.LocationRepositoryCustom;
import com.a1stream.domain.entity.Location;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface LocationRepository extends JpaExtensionRepository<Location, Long>,LocationRepositoryCustom {

    Location findByLocationId(Long locationId);

    Location findByLocationIdAndSiteId(Long locationId, String siteId);

    List<Location> findBySiteId(String siteId);

    List<Location> findByLocationIdIn(Set<Long> locationIds);

    Location findByFacilityIdAndSiteIdAndLocationCd(Long facilityId, String siteId, String mainLocation);

    List<Location> findByLocationIdInAndFacilityId(Set<Long> locationId, Long facilityId);

    List<Location> findBySiteIdAndFacilityIdAndLocationCd(String siteId, Long facilityId, String locationCd);

    @Query(value = "     SELECT * FROM location               "
                 + "      WHERE site_id = :siteId             "
                 + "        AND facility_id = :facilityId     "
                 + "        AND location_type = :locationType "
                 + "   ORDER BY location_cd                   "
                 + "      LIMIT 1                             ", nativeQuery=true)
    Location getLocationVO(@Param("siteId") String siteId, @Param("facilityId") Long facilityId, @Param("locationType") String locationType);

    List<Location> findBySiteIdAndFacilityIdAndLocationCdIn(String siteId, Long facilityId, List<String> locationCds);

    List<Location> findBySiteIdAndBinTypeId(String siteId,Long binTypeId);

    Integer countByFacilityIdAndSiteId(Long facilityId, String siteId);

    List<Location> findBySiteIdAndFacilityId(String siteId, Long facilityId);

    List<Location> findBySiteIdAndLocationCdIn(String siteId, Set<String> locationCds);

    List<Location> findByWorkzoneIdAndSiteId(Long workzoneId, String siteId);
}
