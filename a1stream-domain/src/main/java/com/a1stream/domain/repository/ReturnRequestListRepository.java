package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ReturnRequestListRepositoryCustom;
import com.a1stream.domain.entity.ReturnRequestList;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReturnRequestListRepository extends JpaExtensionRepository<ReturnRequestList, Long>, ReturnRequestListRepositoryCustom {


    @Query(value = " SELECT *                    " +
            "   FROM return_request_list         " +
            "  WHERE site_id                = ?1 " +
            "    AND expire_date           <= ?2 " +
            "    AND request_status         = ?3 "
            , nativeQuery = true )
    List<ReturnRequestList> getReturnRequestListForExpdateAndStatus(String siteId, String systemDate, String status);

    List<ReturnRequestList> findBySiteIdAndFacilityIdAndRequestStatusIn(String siteId, Long facilityId, List<String> status);

    ReturnRequestList findByReturnRequestListId(Long returnRequestListId);

    ReturnRequestList findByReturnRequestListIdAndRequestStatusNotAndSiteId(Long returnRequestListId, String requestStatus, String siteId);
}
