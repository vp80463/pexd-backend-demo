package com.a1stream.master.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.master.CMM050301BO;
import com.a1stream.domain.form.master.CMM050301Form;
import com.a1stream.master.service.CMM0503Service;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class CMM0503Facade {

    @Resource
    private CMM0503Service cmm0503Service;

    public List<CMM050301BO> searchPartsMiddleGroupList(CMM050301Form model) {

        return cmm0503Service.searchPartsMiddleGroupList(model);
    }
}