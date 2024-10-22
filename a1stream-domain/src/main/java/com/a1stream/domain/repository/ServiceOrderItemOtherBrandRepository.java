package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceOrderItemOtherBrandRepositoryCustom;
import com.a1stream.domain.entity.ServiceOrderItemOtherBrand;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceOrderItemOtherBrandRepository extends JpaExtensionRepository<ServiceOrderItemOtherBrand, Long>, ServiceOrderItemOtherBrandRepositoryCustom {

    List<ServiceOrderItemOtherBrand> findByServiceOrderId(Long serviceOrderId);

    List<ServiceOrderItemOtherBrand> findByServiceOrderIdAndProductClassificationAndSiteId(Long serviceOrderId,String type,String siteId);

    List<ServiceOrderItemOtherBrand> findByServiceOrderIdAndProductClassificationAndSiteIdAndSettleType(Long serviceOrderId,String type,String siteId,String settleType);
}
