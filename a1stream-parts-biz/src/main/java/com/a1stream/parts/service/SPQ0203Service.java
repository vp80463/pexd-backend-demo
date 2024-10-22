package com.a1stream.parts.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPQ020301BO;
import com.a1stream.domain.bo.parts.SPQ020302BO;
import com.a1stream.domain.form.parts.SPQ020301Form;
import com.a1stream.domain.form.parts.SPQ020302Form;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/28   Ruan Hansheng     New
*/
@Service
public class SPQ0203Service {

    @Resource
    public InvoiceRepository invoiceRepository;

    @Resource
    public InvoiceItemRepository invoiceItemRepository;

    public Page<SPQ020301BO> getInvoiceList(SPQ020301Form form, PJUserDetails uc) {

        return invoiceRepository.getInvoiceList(form, uc);
    }

    public Page<SPQ020302BO> getInvoiceItemList(SPQ020302Form form, PJUserDetails uc) {

        return invoiceItemRepository.getInvoiceItemList(form, uc);
    }
}
