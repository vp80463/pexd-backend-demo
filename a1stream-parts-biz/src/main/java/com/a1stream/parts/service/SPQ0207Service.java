package com.a1stream.parts.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ020701BO;
import com.a1stream.domain.form.parts.SPQ020701Form;
import com.a1stream.domain.repository.SalesOrderCancelHistoryRepository;

import jakarta.annotation.Resource;
/**
*
* 功能描述:Parts Cancel History Inquiry
*
* mid2330
* 2024年6月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/25   Liu Chaoran   New
*/
@Service
public class SPQ0207Service {

    @Resource
    private SalesOrderCancelHistoryRepository salesOrderCancelHistoryRepo;

    public Page<SPQ020701BO> findPartsCancelHisList(SPQ020701Form form, String siteId) {

        return salesOrderCancelHistoryRepo.findPartsCancelHisList(form, siteId);
    }

    public List<SPQ020701BO> findPartsCancelHisExport(SPQ020701Form form, String siteId) {

        return salesOrderCancelHistoryRepo.findPartsCancelHisExport(form, siteId);
    }
}
