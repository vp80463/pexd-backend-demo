package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ030501BO;
import com.a1stream.domain.bo.parts.SPQ030501PrintDetailBO;
import com.a1stream.domain.form.parts.SPQ030501Form;
import com.a1stream.domain.repository.ProductStockStatusRepository;

import jakarta.annotation.Resource;

/**
 *
* 功能描述:Parts On-Working Check List Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@Service
public class SPQ0305Service {

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    public List<SPQ030501BO> getPartsOnWorkingCheckList(SPQ030501Form form, String siteId) {
        return productStockStatusRepository.getPartsOnWorkingCheckList(form, siteId);
    }

    public List<SPQ030501PrintDetailBO> getPartsOnWorkingCheckPrintList(SPQ030501Form form, String siteId) {
        return productStockStatusRepository.getPartsOnWorkingCheckPrintList(form, siteId);
    }

}
