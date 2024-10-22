package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.InvoiceRepositoryCustom;
import com.a1stream.domain.entity.Invoice;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;



@Repository
public interface InvoiceRepository extends JpaExtensionRepository<Invoice, Long>, InvoiceRepositoryCustom {


    @Query(value = " SELECT * FROM invoice            "
            + " WHERE site_id = :siteId               "
            + "   AND from_facility_id = :facilityId  "
            + "   AND invoice_type = :type            "
            + "   AND invoice_no = :invoiceNo   ", nativeQuery=true)
   Invoice searchInvoiceInfoByInvoiceNo(@Param("siteId") String siteId
                                      , @Param("facilityId") Long facilityId
                                      , @Param("type") String type
                                      , @Param("invoiceNo") String invoiceNo);

    List<Invoice> findByInvoiceIdIn(Set<Long> invoiceId);

    Invoice findByInvoiceId(Long invoiceId);

    Invoice searchInvoiceByInvoiceNoAndSiteIdAndFromFacilityId(String invoiceNo, String siteId, Long fromFacilityId);

}
