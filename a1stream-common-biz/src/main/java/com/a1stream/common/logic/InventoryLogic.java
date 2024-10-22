package com.a1stream.common.logic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.listener.model.ThreadLocalPJAuditableDetailAccessor;
import com.a1stream.domain.vo.ProductStockStatusVO;

@Component
public class InventoryLogic {

    /**
     *
     * @param siteId
     * @param facilityId
     * @param productId
     * @param targetQty
     * @param targetStockStatus
     * @param stockStatusVOChangeMap
     */
    public void generateStockStatusVOMap(String siteId
                                        , Long facilityId
                                        , Long productId
                                        , BigDecimal targetQty
                                        , String targetStockStatus
                                        , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        Map<Long, ProductStockStatusVO> valueMap = stockStatusVOChangeMap.containsKey(targetStockStatus) ? stockStatusVOChangeMap.get(targetStockStatus) : new HashMap<>();

        ProductStockStatusVO stockStatusVO = valueMap.get(productId);

        if (stockStatusVO == null){

            stockStatusVO = new ProductStockStatusVO();

            stockStatusVO.setSiteId(siteId);
            stockStatusVO.setProductId(productId);
            stockStatusVO.setFacilityId(facilityId);
            stockStatusVO.setQuantity(targetQty);
            stockStatusVO.setUpdateProgram(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
        } else {

            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
            stockStatusVO.setQuantity(stockStatusVO.getQuantity().add(targetQty));
            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
        }

        valueMap.put(productId, stockStatusVO);
        stockStatusVOChangeMap.put(targetStockStatus, valueMap);
    }
}