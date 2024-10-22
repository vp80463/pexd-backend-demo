package com.a1stream.master.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMM040301BO;
import com.a1stream.domain.form.master.CMM040301Form;
import com.a1stream.domain.repository.MstProductRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:Model Info Inquiry明细画面
*
* mid2330
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/19   Liu Chaoran     New
*/
@Service
public class CMM0403Service {

    @Resource
    private MstProductRepository mstProductRepository;

    public Page<CMM040301BO> getModelInfoInquiry(CMM040301Form form, String siteId) {
        return mstProductRepository.getModelInfoInquiry(form, siteId);
    }

}