package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.parts.SPM030301BO;
import com.a1stream.domain.bo.parts.SPQ030101BO;
import com.a1stream.domain.form.parts.SPM030301Form;
import com.a1stream.domain.form.parts.SPQ030101Form;

public interface StoringLineItemRepositoryCustom {

    public List<SPQ030101BO> getPartsReceiveListDetail(SPQ030101Form form, String siteId);

    /**
     * author: wang_nan
     */
    public List<SPM030301BO> getPartsStockRegisterList(SPM030301Form form);
}
