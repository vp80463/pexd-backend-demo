package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ReceiptManifestRepositoryCustom;
import com.a1stream.domain.entity.ReceiptManifest;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReceiptManifestRepository extends JpaExtensionRepository<ReceiptManifest, Long>, ReceiptManifestRepositoryCustom {

    List<ReceiptManifest> findByReceiptManifestIdIn(Set<Long> receiptManifestIds);

    ReceiptManifest findByReceiptManifestId(Long receiptManifestId);

    ReceiptManifest findFirstBySiteIdAndSupplierShipmentNo(String siteId, String supplierShipmentNo);

    ReceiptManifest findFirstBySiteIdAndSupplierInvoiceNo(String siteId, String supplierInvoiceNo);

    List<ReceiptManifest> findBySiteIdAndSupplierShipmentNoIn(String siteId, Set<String> supplierShipmentNo);

    @Query(value="select count(1) from receipt_manifest "
        + "where supplier_invoice_no =:supplierInvoiceNo"
        + "and siteId = :siteId ", nativeQuery=true)
    Integer countBySiteIdAndSupplierInvoiceNo(@Param("supplierInvoiceNo") String supplierInvoiceNo,
                                             @Param("siteId") String siteId);

    @Query(value="select count(1) from receipt_manifest "
        + "where supplier_shipment_no =:supplierShipmentNo"
        + "and siteId = :siteId ", nativeQuery=true)
    Integer countBySiteIdAndSupplierShipmentNo(@Param("supplierShipmentNo") String supplierShipmentNo,
                                              @Param("siteId") String siteId);
}
