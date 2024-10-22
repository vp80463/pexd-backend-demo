package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.common.FacilityBO;

public interface MstFacilityRelationRepositoryCustom {

    List<FacilityBO> findFacilityListByRelationType(String siteId
                                                  , Long fromFacilityId
                                                  , String facilityRelationTypeId);
}
