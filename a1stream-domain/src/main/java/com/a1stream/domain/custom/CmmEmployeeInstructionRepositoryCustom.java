package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.unit.SDM030501BO;
import com.a1stream.domain.form.unit.SDM030501Form;

public interface CmmEmployeeInstructionRepositoryCustom {

    List<SDM030501BO> getEmployeeInstructionList(SDM030501Form form);
}
