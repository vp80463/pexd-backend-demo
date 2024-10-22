package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ050201BO;
import com.a1stream.domain.custom.SpCustomerDwRepositoryCustom;
import com.ymsl.solid.jpa.tenant.annotation.MultiTenant;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class SPQ0502Service{

    @Resource
    private SpCustomerDwRepositoryCustom spCustomerDwRepositoryDao;

   @MultiTenant("a1stream-mi-db")
    public List<SPQ050201BO> retrievePartsMIList(String siteId, String pointCd, String year, String month, String customerCd) {

        return spCustomerDwRepositoryDao.findPartsMIList(siteId, pointCd, year, month, customerCd);
    }

}
