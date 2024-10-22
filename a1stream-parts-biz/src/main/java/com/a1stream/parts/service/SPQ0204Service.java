/**
 *
 */
package com.a1stream.parts.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ020402BO;
import com.a1stream.domain.bo.parts.SPQ020403BO;
import com.a1stream.domain.entity.Invoice;
import com.a1stream.domain.form.parts.SPQ020401Form;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.vo.InvoiceVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPQ0204Service {

    @Resource
    private InvoiceRepository invoiceRepository;

    @Resource
    private InvoiceItemRepository invoiceItemRepository;

    public List<SPQ020402BO> searchInvoiceInfo(SPQ020401Form model) {

        return invoiceRepository.searchInvoiceInfo(model);
    }

    public List<SPQ020403BO> searchInvoiceDetailInfo(SPQ020401Form model) {

        return invoiceItemRepository.searchInvoiceInfoDetail(model);
    }

    public List<InvoiceVO> findInvoiceById(Set<Long> invoiceId) {

        return BeanMapUtils.mapListTo(invoiceRepository.findByInvoiceIdIn(invoiceId), InvoiceVO.class);
    }

    public void saveInvoiceInfo(List<InvoiceVO> updateResult) {

        invoiceRepository.saveInBatch(BeanMapUtils.mapListTo(updateResult, Invoice.class));
    }
}
