package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.HomePageForm;
import com.a1stream.common.model.HomePagePointBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.batch.PartsDeadStockItemBO;
import com.a1stream.domain.bo.master.CMM020501BO;
import com.a1stream.domain.bo.unit.SDM010901BO;
import com.a1stream.domain.vo.MstFacilityVO;

public interface MstFacilityRepositoryCustom {

    ValueListResultBO findPointList(PJUserDetails uc, BaseVLForm model);
    
    List<CmmHelperBO> getPointList(PJUserDetails uc, BaseVLForm model);

    List<CMM020501BO> findPointListBySiteId(String siteId);

    List<PartsDeadStockItemBO> getAllNoneReturnWarehousePoints(String siteId);

    List<MstFacilityVO> getFacilityByDateList(String siteId,String collectDate);

    List<HomePagePointBO> findPointListForHome(HomePageForm form);

    List<SDM010901BO> getPointList();
}
