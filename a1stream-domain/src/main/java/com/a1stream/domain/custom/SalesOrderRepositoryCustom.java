package com.a1stream.domain.custom;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.a1stream.domain.bo.common.EInvoiceInvoiceBO;
import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.parts.DIM020401BO;
import com.a1stream.domain.bo.parts.SPM020101BO;
import com.a1stream.domain.bo.parts.SPM020103PrintBO;
import com.a1stream.domain.bo.parts.SPM021301BO;
import com.a1stream.domain.bo.parts.SPQ020101BO;
import com.a1stream.domain.bo.parts.SPQ020201BO;
import com.a1stream.domain.bo.service.SVM0102PrintServicePartBO;
import com.a1stream.domain.bo.unit.SDM030101BO;
import com.a1stream.domain.bo.unit.SDM030103BO;
import com.a1stream.domain.form.parts.SPM020101Form;
import com.a1stream.domain.form.parts.SPM021301Form;
import com.a1stream.domain.form.parts.SPQ020101Form;
import com.a1stream.domain.form.parts.SPQ020201Form;
import com.a1stream.domain.form.unit.SDM030101Form;

/**
 * @author dong zhen
 */
public interface SalesOrderRepositoryCustom {

    Integer getSalesOrderCountForSp(String siteId, List<String> orderStatusList);

    Integer getSalesOrderCountForSd(String siteId, String orderStatus);

    SPM020103PrintBO getFastSalesOrderReportData(Long salesOrderId);

    PageImpl<SPM020101BO> searchSalesOrderList(SPM020101Form model);

    List<DIM020401BO> getSparePartsDownloadInfo(Long facilityId, String siteId);

    List<DIM020401BO> getServiceDownloadInfo(Long facilityId, String siteId);

    List<DIM020401BO> getPartsStoringDownloadInfo(Long facilityId, String siteId);

    Integer countSalesOrder(Long facilityId, String siteId);

    Integer countServiceOrder(Long facilityId, String siteId);

    public Page<SPQ020101BO> findSalesOrderCustomerList(SPQ020101Form form, String siteId);

    public List<SPQ020101BO> findSalesOrderCustomerExportList(SPQ020101Form form, String siteId);

    public Page<SPQ020201BO> findSalesOrderPartsList(SPQ020201Form form, String siteId);

    public List<SPQ020201BO> findSalesOrderPartsExportList(SPQ020201Form form, String siteId);

    Page<SPM021301BO> searchBackOrderList(SPM021301Form form);

    List<SVM0102PrintServicePartBO> getServicePartPrintList(Long serviceOrderId);

    List<SVM0102PrintServicePartBO> getServicePaymentPartPrintList(Long serviceOrderId);

    List<SVM0102PrintServicePartBO> getServicePaymentPartForDoPrintList(Long serviceOrderId);

    List<SDM030101BO> findSalesOrderData(SDM030101Form form, String siteId);

    SDM030103BO getDealerWholeSOBasicInfo(Long orderId);

    public List<EInvoiceProductsBO> getMCSpecialProductsInfo(Long orderId,String flag);

    public List<EInvoiceProductsBO> getMCProductsInfo(Long orderId,String flag);

    public EInvoiceInvoiceBO getInvoiceinfoForParts(Long orderId);

    public EInvoiceInvoiceBO getSpecialMCInvoiceinfo(Long orderId);

    public EInvoiceInvoiceBO getMCInvoiceinfo(Long orderId);

}
