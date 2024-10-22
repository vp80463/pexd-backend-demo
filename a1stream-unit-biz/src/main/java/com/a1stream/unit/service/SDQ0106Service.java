package com.a1stream.unit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDQ010602BO;
import com.a1stream.domain.bo.unit.SDQ010602DetailBO;
import com.a1stream.domain.form.unit.SDQ010602Form;
import com.a1stream.domain.repository.ReceiptSlipRepository;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Receipt Report (Detail)
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/09/06   Wang Nan      New
*/
@Service
public class SDQ0106Service {

    @Resource
    private ReceiptSlipRepository receiptSlipRepo;

    public SDQ010602BO getDetail(SDQ010602Form form) {
        return receiptSlipRepo.getFastReceiptReportDetail(form);
    }

    public List<SDQ010602DetailBO> getDetailList(SDQ010602Form form) {
        return receiptSlipRepo.getFastReceiptReportDetailList(form);
    }

}
