package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMM020201BO;
import com.a1stream.domain.bo.master.CMM020202GridBO;

public interface CmmPersonRepositoryCustom {

    ValueListResultBO findEmployeeList(BaseVLForm model, String siteId);
    
    List<CmmHelperBO> getEmployeeList(BaseVLForm model, String siteId);

    List<CMM020201BO> findEmployeeInfoList(String siteId);

    List<CMM020202GridBO> getPointDetail(Long employeeId, String siteId);
}
