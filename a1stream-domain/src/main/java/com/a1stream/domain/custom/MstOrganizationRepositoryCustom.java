package com.a1stream.domain.custom;

import java.util.List;
import java.util.Set;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.vo.MstOrganizationVO;

public interface MstOrganizationRepositoryCustom {

    ValueListResultBO findSupplierList(String siteId, BaseVLForm model);
    
    List<CmmHelperBO> getSupplierList(String siteId, BaseVLForm model);

    MstOrganizationVO getPartSupplier(String siteId);

    List<MstOrganizationVO> getPartSupplierList(Set<String> siteIdSet);
}
