package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.InventoryTransactionCustom;
import com.a1stream.domain.entity.InventoryTransaction;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface InventoryTransactionRepository extends JpaExtensionRepository<InventoryTransaction, Long>, InventoryTransactionCustom {

    /**
     * @param siteId
     * @param transactionDate
     * @param transactionType
     * @param facilityId
     * @param partsIds
     * @return
     */
    @Query(value = " SELECT product_id AS productCd "+
            " FROM inventory_transaction " +
            " WHERE site_id = ?1" +
            " AND physical_transaction_date > ?2" +
            " AND inventory_transaction_type  = ?3" +
            " AND target_facility_id  = ?4" +
            " AND product_id  in (?5)"
            , nativeQuery = true )
    public List<Long> getAllStockAjustPartsOnPoint(String siteId, String transactionDate, String transactionType, Long facilityId,Set<Long> partsIds);

    @Query(value = " SELECT location_id AS locationId "+
            " FROM inventory_transaction " +
            " WHERE site_id = ?1" +
            " AND (from_facility_id = ?2 OR to_facility_id = ?2)" +
            " AND location_id = ?3"
            , nativeQuery = true )
    List<Long> isLocationInUse(String siteId, Long facilityId, Long locationId);
}
