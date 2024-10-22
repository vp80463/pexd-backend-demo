package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.bo.unit.SDM050101BO;
import com.a1stream.domain.bo.unit.SDM050102BO;
import com.a1stream.domain.bo.unit.SDM050201BO;
import com.a1stream.domain.bo.unit.SDM050202BO;
import com.a1stream.domain.bo.unit.SDM050202HeaderBO;
import com.a1stream.domain.bo.unit.SDM050601BO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.bo.unit.SDQ050101BO;
import com.a1stream.domain.form.unit.SDM050101Form;
import com.a1stream.domain.form.unit.SDM050102Form;
import com.a1stream.domain.form.unit.SDM050201Form;
import com.a1stream.domain.form.unit.SDM050202Form;
import com.a1stream.domain.form.unit.SDM050601Form;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.form.unit.SDQ050101Form;

/**
*
* @author mid2330
*/
public interface CmmUnitPromotionListRepositoryCustom {

    public ValueListResultBO findPromotionValueList(BaseVLForm model);

    public Page<SDQ050101BO> findPromotionMCList(SDQ050101Form form);

    public List<SDQ050101BO> findPromotionMCListExport(SDQ050101Form form);

    public List<SDM050101BO> findSetUpPromotionList(SDM050101Form form);

    public List<SDM050101BO> findSetUpPromotionListExport(SDM050101Form form);

    public List<SDM050102BO> findPromotionMC(SDM050102Form form);

    List<SDM050601BO> getUpdPeriodMaintList(SDM050601Form form);

    public List<SDM050201BO> findSetUpPromotionTerms(SDM050201Form form);

    public List<SDM050202BO> findUploadPromoQC(SDM050202Form form);

    public SDM050202HeaderBO findUploadPromoQCHeader(SDM050202Form form);

    SDM050801BO getActivePromotionList(SDM050801Form form);

    List<SDM010601BO> getEffectivePromotionInfoList(String sysDate);
}
