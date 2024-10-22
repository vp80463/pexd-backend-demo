/**
 *
 */
package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPM020202FunctionBO;
import com.a1stream.domain.form.parts.SPM020202Form;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.vo.InvoiceVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Sales Return History Inquiry
*
* mid2330
* 2024年7月2日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/02   Liu Chaoran   New
*/
@Service
public class SPM020202Service {

    @Resource
    InvoiceRepository invoiceRepository;

    @Resource
    InvoiceItemRepository invoiceItemRepository;

    public List<SPM020202FunctionBO> getSalesReturnHistoryHeaderList(SPM020202Form form, String siteId) {
        return invoiceRepository.getSalesReturnHistoryHeaderList(form, siteId);
     }

    public List<SPM020202FunctionBO> getSalesReturnHistoryDetailList(SPM020202Form form, String siteId) {
        return invoiceItemRepository.getSalesReturnHistoryDetailList(form, siteId);
     }

    public InvoiceVO searchInvoiceByInvoiceNoAndFromFacilityId(String invoiceNo, String siteId, Long pointId) {
        return BeanMapUtils.mapTo(invoiceRepository.searchInvoiceByInvoiceNoAndSiteIdAndFromFacilityId(invoiceNo, siteId, pointId), InvoiceVO.class);
    }
}
