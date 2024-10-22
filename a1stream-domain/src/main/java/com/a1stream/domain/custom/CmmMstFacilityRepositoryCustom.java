package com.a1stream.domain.custom;
import java.util.List;

import com.a1stream.domain.bo.master.CMM020501BO;

public interface CmmMstFacilityRepositoryCustom {

    List<CMM020501BO> findPointList(String siteId);


}
