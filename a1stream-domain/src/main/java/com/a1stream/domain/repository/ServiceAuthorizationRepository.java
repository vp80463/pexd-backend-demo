package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ServiceAuthorization;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceAuthorizationRepository extends JpaExtensionRepository<ServiceAuthorization, Long> {

    @Query("SELECT EXISTS (  "
    		+ "  SELECT 1   "
    		+ " FROM ServiceAuthorization  "
    	    + "  WHERE siteId = ?1  "
    	    + " AND pointId = ?2  "
    	    + " AND serializedItemNo = ?3"
    	    + " AND fromDate <= ?4 "
    	    + " AND toDate >= ?5 "
    	    + " AND authorizationNo IN (?6) )AS exists_result " )
    Boolean existAuthorization(String siteId
				    		, Long pointId
				    		, String serializedItemNo
				    		, String sysDateFrom
				    		, String sysDateTo
				    		, List<String> authorizationNoList);

    List<ServiceAuthorization> findBySiteIdInAndAuthorizationNoIn(Set<String> siteId, Set<String> authorizationNos);
}
