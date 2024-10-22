package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstFacilityRelationRepositoryCustom;
import com.a1stream.domain.entity.MstFacilityRelation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstFacilityRelationRepository extends JpaExtensionRepository<MstFacilityRelation, Long>, MstFacilityRelationRepositoryCustom {

    List<MstFacilityRelation> findBySiteIdAndFromFacilityId(String siteId
                                                          , Long facilityId);
}
