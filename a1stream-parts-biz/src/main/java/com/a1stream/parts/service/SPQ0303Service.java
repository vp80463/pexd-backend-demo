package com.a1stream.parts.service;

import com.a1stream.domain.bo.parts.SPQ030301BO;
import com.a1stream.domain.bo.parts.SPQ030302BO;
import com.a1stream.domain.form.parts.SPQ030301Form;
import com.a1stream.domain.form.parts.SPQ030302Form;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 
* 功能描述:Parts Stock Information Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@Service
public class SPQ0303Service {

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepositoryCustom;

    public Page<SPQ030301BO> getPartsStockListPageable(SPQ030301Form form, String siteId) {
        return productStockStatusRepository.getPartsStockListPageable(form, siteId);
    }

    public Page<SPQ030302BO> getPartsStockDetailListPageable(SPQ030302Form form, String siteId) {
        return productInventoryRepositoryCustom.getPartsStockDetailListPageable(form, siteId);
    }

}
