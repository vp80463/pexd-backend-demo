package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.unit.SDM050301BO;
import com.a1stream.domain.bo.unit.SDM050401BO;
import com.a1stream.domain.bo.unit.SDM050501BO;
import com.a1stream.domain.bo.unit.SDM050701BO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.form.unit.SDM050301Form;
import com.a1stream.domain.form.unit.SDM050401Form;
import com.a1stream.domain.form.unit.SDM050501Form;
import com.a1stream.domain.form.unit.SDM050701Form;
import com.a1stream.domain.form.unit.SDM050801Form;

public interface CmmPromotionOrderRepositoryCustom {

    Page<SDM050701BO> getWaitingScreenList(SDM050701Form form);

    Page<SDM050301BO> getPromotionResult(SDM050301Form form);

    Page<SDM050401BO> getPromotionJudgement(SDM050401Form form);

    List<SDM050801BO> getCpoPromoRecList(SDM050801Form form);

    SDM050501BO getInitResult(SDM050501Form form);
}
