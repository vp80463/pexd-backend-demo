package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.parts.SPM031501BO;
import com.a1stream.domain.form.parts.SPM031501Form;

public interface StoringLineRepositoryCustom {

    /**
     * author: wang_nan
     */
    public List<SPM031501BO> getPartsStockRegisterMultiLinesList(SPM031501Form form);
}
