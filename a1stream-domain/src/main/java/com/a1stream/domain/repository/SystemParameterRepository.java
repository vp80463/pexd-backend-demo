package com.a1stream.domain.repository;

import com.a1stream.domain.entity.SystemParameter;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SystemParameterRepository extends JpaExtensionRepository<SystemParameter, Long> {

    SystemParameter findBySystemParameterTypeId(String businessType);

    @Query(value="select * from system_parameter "
               + "where site_id =:siteId "
               + "and facility_id =:facilityId "
               + "and system_parameter_type_id  =:paramTypeId "
               + "and parameter_value = :paramValue", nativeQuery=true)
    SystemParameter getProcessingSystemParameter(@Param("siteId") String siteId
                                                     , @Param("facilityId") Long facilityId
                                                     , @Param("paramTypeId") String paramTypeId
                                                     , @Param("paramValue") String paramValue);

    SystemParameter findBySiteIdAndFacilityIdAndSystemParameterTypeId(String siteId, Long facilityId, String systemParameterTypeId);

    List<SystemParameter> findBySiteIdAndSystemParameterTypeIdIn(String siteId, List<String> systemParameterTypeId);

    SystemParameter findSystemParameterBySiteIdAndSystemParameterTypeId(String siteId, String systemParameterTypeId);

    SystemParameter findBySiteIdAndSystemParameterTypeIdAndParameterValue(String siteId, String systemParameterTypeId, String parameterValue);
}
