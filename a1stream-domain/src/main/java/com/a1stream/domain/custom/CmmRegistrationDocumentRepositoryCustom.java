package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.PageImpl;

import com.a1stream.domain.bo.unit.SDM040103BO;
import com.a1stream.domain.bo.unit.SDQ040103BO;
import com.a1stream.domain.form.unit.SDQ040103Form;

public interface CmmRegistrationDocumentRepositoryCustom {

    PageImpl<SDQ040103BO> pageWarrantyCardInfo(SDQ040103Form model, String siteId);

    List<SDQ040103BO> listWarrantyCardInfo(SDQ040103Form model, String siteId);

    SDM040103BO getWarrantyCardBasicInfo(Long registrationDocumentId);
}
