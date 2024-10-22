package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.parts.SPQ030201BO;
import com.a1stream.domain.bo.parts.SPQ030401BO;
import com.a1stream.domain.bo.unit.SDQ011401BO;
import com.a1stream.domain.form.parts.SPQ030201Form;
import com.a1stream.domain.form.parts.SPQ030401Form;
import com.a1stream.domain.form.unit.SDQ011401Form;

public interface InventoryTransactionCustom {

    public List<SPQ030401BO> findPartsInOutHistoryList(SPQ030401Form model, String siteId);

    public List<SPQ030201BO> findPartsAdjustmentHistoryList(SPQ030201Form model, String siteId);

    Page<SDQ011401BO> getStockInOutHistoryList(SDQ011401Form form);

}
