package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ050301BO;
import com.a1stream.domain.custom.SpGeneralyyyyRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ050301Form;
import com.ymsl.solid.jpa.tenant.annotation.MultiTenant;

import jakarta.annotation.Resource;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Service
public class SPQ0503Service{

    @Resource
    private SpGeneralyyyyRepositoryCustom spGeneralyyyyDao;

    @MultiTenant("a1stream-mi-db")
    public List<SPQ050301BO> retrievePartsMIList(SPQ050301Form model) {

        return spGeneralyyyyDao.findPartsMIList(model);
    }

}
