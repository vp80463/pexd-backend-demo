package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServicePaymentRepositoryCustom;
import com.a1stream.domain.entity.ServicePayment;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServicePaymentRepository extends JpaExtensionRepository<ServicePayment, Long>, ServicePaymentRepositoryCustom {

    ServicePayment findByPaymentIdAndSiteId(Long paymentId, String siteId);

    @Query(value = "  SELECT * FROM service_payment "
                + " WHERE site_id =:siteId "
                + " AND factory_payment_control_no =:controlNo "
                + " AND payment_category = :category "
                + " AND (bulletin_no = :bulletinNo || :bulletinNo = '')", nativeQuery = true)
    List<ServicePayment> findSvPaymentList(@Param("siteId") String siteId
                                        , @Param("controlNo") String controlNo
                                        , @Param("category") String category
                                        , @Param("bulletinNo") String bulletinNo);
}
