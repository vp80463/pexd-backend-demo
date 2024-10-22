package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.PartsStoringListForWarehousePrintDetailBO;
import com.a1stream.domain.bo.parts.SPQ030101BO;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.bo.unit.SDQ010602BO;
import com.a1stream.domain.bo.unit.SDQ010602DetailBO;
import com.a1stream.domain.form.parts.SPQ030101Form;
import com.a1stream.domain.form.unit.SDM010601Form;
import com.a1stream.domain.form.unit.SDQ010602Form;

public interface ReceiptSlipRepositoryCustom {

    List<SPQ030101BO> getReceiptSlipList(SPQ030101Form form, String siteId);

    List<SPQ030101BO> getReceiptSlipListByReceiptSlipItemId(List<Long> list, String siteId);

    List<SPQ030101BO> getPartsReceiveAndRegisterPrintList(SPQ030101Form form, String siteId);

    Integer countPartsStoring(Long facilityId, String siteId);

    List<PartsStoringListForWarehousePrintDetailBO> getPrintPartsStoringListForWarehouseData(List<Long> receiptSlipId);

    List<PartsStoringListForFinancePrintDetailBO> getPartsStoringListForFinanceReport(List<Long> receiptSlipIds);

    PartsStoringListForFinancePrintBO getPartsStoringListForFinanceReportHeader(List<Long> receiptSlipIds);

    List<SDM010601BO> getFastReceiptReportList(SDM010601Form form);

    SDQ010602BO getFastReceiptReportDetail(SDQ010602Form form);

    List<SDQ010602DetailBO> getFastReceiptReportDetailList(SDQ010602Form form);

}
