package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.parts.SPQ020601BO;
import com.a1stream.domain.bo.parts.SPQ020601PrintBO;
import com.a1stream.domain.bo.parts.SPQ020602BO;
import com.a1stream.domain.form.parts.SPQ020601Form;
import com.a1stream.domain.form.parts.SPQ020602Form;

public interface PickingListRepositoryCustom {

    Page<SPQ020601BO> getPickingInstructionList(SPQ020601Form form);

    List<SPQ020601BO> getPickingInstructionExportData(SPQ020601Form form);

    List<SPQ020602BO> getPickingInstructionDetailList(SPQ020602Form form);

    List<SPQ020601BO> getPartsPickingListReport(String siteId, SPQ020601Form form);

    SPQ020601PrintBO getPartsPickingListReportHeader(String siteId, SPQ020601Form form);
}
