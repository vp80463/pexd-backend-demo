package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM030102BO;
import com.a1stream.domain.form.parts.SPM030102Form;

public interface ReceiptManifestItemRepositoryCustom {

    List<SPM030102BO> getReceiptManifestItemList(SPM030102Form form, PJUserDetails uc);
}
