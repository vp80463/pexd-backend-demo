package com.a1stream.parts.facade;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.parts.SPQ030301BO;
import com.a1stream.domain.bo.parts.SPQ030302BO;
import com.a1stream.domain.form.parts.SPQ030301Form;
import com.a1stream.domain.form.parts.SPQ030302Form;
import com.a1stream.parts.service.SPQ0303Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

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
@Component
public class SPQ0303Facade {

    @Resource
    private SPQ0303Service spq0303Service;

    public Page<SPQ030301BO> getPartsStockListPageable(SPQ030301Form form, String siteId) {
        this.check(form.getParts(), form.getPartsId());
        return spq0303Service.getPartsStockListPageable(form, siteId);
    }

    public Page<SPQ030302BO> getPartsStockDetailListPageable(SPQ030302Form form, String siteId) {
        this.check(form.getParts(), form.getPartsId());
        return spq0303Service.getPartsStockDetailListPageable(form, siteId);
    }

    private void check(String parts, Long partsId) {

        //检查parts
        if (StringUtils.isNotBlank(parts) && Nulls.isNull(partsId)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             parts,
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }
    }
}
