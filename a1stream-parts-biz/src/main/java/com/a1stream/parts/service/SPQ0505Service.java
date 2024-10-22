package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ050501BO;
import com.a1stream.domain.custom.SpSalesDwRepositoryCustom;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.jpa.tenant.annotation.MultiTenant;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class SPQ0505Service{

    @Resource
    private SpSalesDwRepositoryCustom spSalesDwRepositoryCustom;

    @MultiTenant("a1stream-mi-db")
    public List<SPQ050501BO> findPartsRetailPriceMIList(String siteId, String pointCd, String year, String month, String customerCd) {

        return BeanMapUtils.mapListTo(spSalesDwRepositoryCustom.findPartsRetailPriceMIList(siteId, pointCd, year, month, customerCd),SPQ050501BO.class);
    }

}
