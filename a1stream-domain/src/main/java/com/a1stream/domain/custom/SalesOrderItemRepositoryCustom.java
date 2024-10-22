package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.parts.SPM020103PrintDetailBO;
import com.a1stream.domain.bo.parts.TargetSalesOrderItemBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.unit.SDM030103DetailBO;

public interface SalesOrderItemRepositoryCustom {

    List<PartDetailBO> listServicePartByOrderId(Long serviceOrderId);

    List<SPM020103PrintDetailBO> getFastSalesOrderReportList(Long salesOrderId);

    List<TargetSalesOrderItemBO> getWaitingAllocateSoItemList(String siteId, Long deliveryPointId, Long productId);

    public List<SDM030103DetailBO> getDealerWholeSODetails(Long orderId);

    List<EInvoiceProductsBO> getServiceProductsModelsForParts(List<Long> productId,String type,Long orderId);
}
