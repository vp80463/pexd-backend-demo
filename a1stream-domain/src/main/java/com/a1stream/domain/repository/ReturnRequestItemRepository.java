package com.a1stream.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ReturnRequestItemRepositoryCustom;
import com.a1stream.domain.entity.ReturnRequestItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReturnRequestItemRepository extends JpaExtensionRepository<ReturnRequestItem, Long>, ReturnRequestItemRepositoryCustom {


    @Query(value = " SELECT *                    " +
            "   FROM return_request_item         " +
            "  WHERE site_id                = ?1 " +
            "    AND return_request_list_id = ?2 " +
            "    AND product_id             = ?3 " +
            "    AND yamaha_invoice_seq_no  = ?4 " +
            "    AND request_type           = ?5 " +
            "    AND return_price           = ?6 "
            , nativeQuery = true )
    List<ReturnRequestItem> getReturnRequestItemForRequestList(String siteId, Long returnListId, Long productId,String seqNo,String type, BigDecimal price);

    List<ReturnRequestItem> findBySiteIdAndReturnRequestListId(String siteId, Long returnListId);

    List<ReturnRequestItem> findByReturnRequestItemIdIn(List<Long> returnRequestItemIdList);
}
