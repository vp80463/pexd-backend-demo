package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.PageImpl;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM030102BO;
import com.a1stream.domain.bo.parts.SPM040401BO;
import com.a1stream.domain.bo.parts.SPQ040101BO;
import com.a1stream.domain.form.parts.SPM030102Form;
import com.a1stream.domain.form.parts.SPM040401Form;
import com.a1stream.domain.form.parts.SPM040402Form;
import com.a1stream.domain.form.parts.SPQ040101Form;

public interface PurchaseOrderRepositoryCustom {

    List<SPM040401BO> getPurchaseOrderList(SPM040401Form form, PJUserDetails uc);

    PageImpl<SPQ040101BO> getPurchaseOrderList(SPQ040101Form model,String siteId);

    SPM040401BO getPurchaseOrder(SPM040402Form form, PJUserDetails uc);

    Integer getPurchaseOrderSize(String siteId, String orderStatus);

    SPM030102BO getPurchaseOrderData(SPM030102Form form, PJUserDetails uc);
}
