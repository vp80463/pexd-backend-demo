package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMM060101BO;
import com.a1stream.domain.form.master.CMM060101Form;
import com.a1stream.domain.repository.MstProductRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class CMM0601Service {

    @Resource
    private MstProductRepository mstProductRepo;

    public List<CMM060101BO> getSvJobData(CMM060101Form model) {

        return mstProductRepo.getSvJobData(model);
    }
}