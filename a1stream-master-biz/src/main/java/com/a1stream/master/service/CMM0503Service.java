package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMM050301BO;
import com.a1stream.domain.form.master.CMM050301Form;
import com.a1stream.domain.repository.MstProductCategoryRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class CMM0503Service {

    @Resource
    private MstProductCategoryRepository mstProductCategoryRepository;

    public List<CMM050301BO> searchPartsMiddleGroupList(CMM050301Form model) {

        return mstProductCategoryRepository.searchProductMiddleGroupList(model);
    }

}