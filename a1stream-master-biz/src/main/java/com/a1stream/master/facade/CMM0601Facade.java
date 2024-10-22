package com.a1stream.master.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.master.CMM060101BO;
import com.a1stream.domain.form.master.CMM060101Form;
import com.a1stream.master.service.CMM0601Service;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class CMM0601Facade {

    @Resource
    private CMM0601Service cmm0601Ser;

    public List<CMM060101BO> getSvJobData(CMM060101Form model) {

        return cmm0601Ser.getSvJobData(model);
    }
}