package com.a1stream.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.PartsPickingListByOrderPrintBO;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.PartsStoringListForWarehousePrintDetailBO;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;

import jakarta.annotation.Resource;

@Service
public class ReportCommonService {

    @Resource
    DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    ReceiptSlipRepository receiptSlipRepository;

    @Resource
    InvoiceRepository invoiceRepository;

    public List<PartsPickingListByOrderPrintBO> getPartsPickingListByOrderReportData(Long deliveryOrderId) {
        return deliveryOrderItemRepository.getPartsPickingListByOrderReportList(deliveryOrderId);
    }

    public List<PartsStoringListForWarehousePrintDetailBO> getPrintPartsStoringListForWarehouseData(List<Long> receiptSlipIds) {
        return receiptSlipRepository.getPrintPartsStoringListForWarehouseData(receiptSlipIds);
    }

    public PartsSalesReturnInvoiceForFinancePrintBO getPartsSalesReturnInvoiceForFinanceData(Long invoiceId) {
        return invoiceRepository.getCommonPartsSalesReturnInvoiceForFinanceData(invoiceId);
     }

    public List<PartsSalesReturnInvoiceForFinancePrintDetailBO> getPartsSalesReturnInvoiceForFinanceDetailList(Long invoiceId) {
        return invoiceRepository.getCommonPartsSalesReturnInvoiceForFinanceDetailList(invoiceId);
     }
}
