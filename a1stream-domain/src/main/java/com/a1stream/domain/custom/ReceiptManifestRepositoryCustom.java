package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM030101BO;
import com.a1stream.domain.bo.unit.SDQ010701BO;
import com.a1stream.domain.bo.unit.SDQ010702HeaderBO;
import com.a1stream.domain.form.parts.SPM030101Form;
import com.a1stream.domain.form.unit.SDQ010701Form;

public interface ReceiptManifestRepositoryCustom {

    List<SPM030101BO> getReceiptManifestList(SPM030101Form form, PJUserDetails uc);

    List<SPM030101BO> getInoviceNoAndCaseNoList(List<String> orderNo);

    List<SDQ010701BO> getReceiptManifestListForSD(SDQ010701Form form);

    SDQ010702HeaderBO getRecManMaintHeader(Long receiptManifestId);

}
