package com.a1stream.domain.custom;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;

/**
* CmmMstOrganization table customer search function
*
* @author mid1439
*/
public interface CmmMstOrganizationRepositoryCustom {

    public ValueListResultBO findCustomerValueList(BaseVLForm model, String siteId);
}
