package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmPromotionOrderRepositoryCustom;
import com.a1stream.domain.entity.CmmPromotionOrder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmPromotionOrderRepository extends JpaExtensionRepository<CmmPromotionOrder, Long>, CmmPromotionOrderRepositoryCustom {

    List<CmmPromotionOrder> findByPromotionOrderIdIn(List<Long> promotionOrderIds);

    CmmPromotionOrder findByPromotionOrderId(Long promotionOrderId);

}
