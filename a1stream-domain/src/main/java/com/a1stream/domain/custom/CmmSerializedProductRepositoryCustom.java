package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.common.model.DemandVLForm;
import com.a1stream.domain.bo.common.DemandBO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.bo.unit.SVM010401BO;
import com.a1stream.domain.bo.unit.SVM010402BO;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.form.unit.SVM010401Form;

public interface CmmSerializedProductRepositoryCustom {

    List<DemandBO> searchSerializedProductList(DemandVLForm model);

    boolean isMcBigBike(Long serializedProId);

    Page<SVM010401BO> getCommonMcData(SVM010401Form form, String siteId);

    SVM010402BO getMcBasicInfo(Long cmmSerializedProductId);

    SDM050801BO getPromotionProduct(SDM050801Form form);
}
