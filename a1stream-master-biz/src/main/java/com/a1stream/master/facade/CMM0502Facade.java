package com.a1stream.master.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.master.CMM050201BO;
import com.a1stream.master.service.CMM0502Service;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class CMM0502Facade {

    @Resource
    private CMM0502Service cmm0502Service;

    public List<CMM050201BO> searchPartsLargeGroupList() {

        return cmm0502Service.searchPartsLargeGroupList();
    }
}