package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.parts.PartsPickingListByOrderPrintBO;
import com.a1stream.domain.bo.parts.SPM030603BO;
public interface DeliveryOrderItemRepositoryCustom {

    List<PartsPickingListByOrderPrintBO> getPartsPickingListByOrderReportList(Long deliveryOrderId);

    List<SPM030603BO> getTransferPartsDetailList(Long deliveryOrderId);
}
