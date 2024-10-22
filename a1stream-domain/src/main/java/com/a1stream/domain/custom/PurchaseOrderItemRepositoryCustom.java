package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM040402BO;
import com.a1stream.domain.form.parts.SPM040402Form;

public interface PurchaseOrderItemRepositoryCustom {

    List<SPM040402BO> getPurchaseOrderItemList(SPM040402Form form, PJUserDetails uc);

}
