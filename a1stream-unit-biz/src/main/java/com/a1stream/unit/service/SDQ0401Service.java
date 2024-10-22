package com.a1stream.unit.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDQ040103BO;
import com.a1stream.domain.form.unit.SDQ040103Form;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class SDQ0401Service{

    @Resource
    private CmmRegistrationDocumentRepository cmmRegistDocRepo;

    public Page<SDQ040103BO> getWarrantyCardPage(SDQ040103Form model, String siteId) {

        return cmmRegistDocRepo.pageWarrantyCardInfo(model, siteId);
    }

    public List<SDQ040103BO> getWarrantyCardList(SDQ040103Form model, String siteId) {

        return cmmRegistDocRepo.listWarrantyCardInfo(model, siteId);
    }
}
