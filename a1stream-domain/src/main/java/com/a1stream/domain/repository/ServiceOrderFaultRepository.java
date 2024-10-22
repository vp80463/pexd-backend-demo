package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceOrderFaultRepositoryCustom;
import com.a1stream.domain.entity.ServiceOrderFault;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceOrderFaultRepository extends JpaExtensionRepository<ServiceOrderFault, Long>, ServiceOrderFaultRepositoryCustom {

    boolean existsBySiteIdAndAuthorizationNoInAndServiceOrderIdNot(String siteId, List<String> authorizationNoList, Long serviceOrderId);

    boolean existsBySiteIdAndAuthorizationNoIn(String siteId, List<String> authorizationNoList);
    
    List<ServiceOrderFault> findByServiceOrderId(Long serviceOrderId);
    
    @Query("SELECT EXISTS (  "
    		+ "  SELECT 1   "
    		+ " FROM ServiceOrderFault  "
    	    + "  WHERE siteId = ?1  "
    	    + " AND authorizationNo IN (?2) "
    	    + " AND serviceOrderId != ?3 )AS exists_result " )
    Boolean existOrderFault(String siteId
    						, List<String> authorizationNoList
				    		, Long serviceOrderId);
}
