package com.a1stream.unit.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDQ070601BO;
import com.a1stream.domain.custom.SdPsiDwRepositoryCustom;
import com.a1stream.domain.form.unit.SDQ070601Form;
import com.ymsl.solid.jpa.tenant.annotation.MultiTenant;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class SDQ0706Service{

    @Resource
    private SdPsiDwRepositoryCustom sdPsiDwRepositoryCustom;

   @MultiTenant("a1stream-mi-db")
    public Page<SDQ070601BO> findSdPsiDwList(SDQ070601Form model) {

        return sdPsiDwRepositoryCustom.findSdPsiDwList(model);
    }
}
