package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmGeorgaphy;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmGeorgaphyRepository extends JpaExtensionRepository<CmmGeorgaphy, Long> {

    List<CmmGeorgaphy> findByGeographyClassificationId(String geographyClassificationId);

    @Query(value="select * from cmm_georgaphy "
            + "where geography_classification_id =:clsId "
            + "and parent_geography_id =:provinceId", nativeQuery=true)
    List<CmmGeorgaphy> getCityByProvince( @Param("clsId") String clsId
                                            , @Param("provinceId") Long provinceId);

    @Query(value="select * from cmm_georgaphy "
            + "where geography_classification_id =:clsId "
            + "and geography_cd in :cityCds", nativeQuery=true)
    List<CmmGeorgaphy> getCityGeographys( @Param("clsId") String clsId
                                            , @Param("cityCds") Set<String> cityCds);

    List<CmmGeorgaphy> findByGeographyNmIn(List<String> geographyNmList);

    CmmGeorgaphy findByGeographyId(Long geographyId);

    List<CmmGeorgaphy> findByGeographyIdIn(Set<Long> geographyIds);
}
