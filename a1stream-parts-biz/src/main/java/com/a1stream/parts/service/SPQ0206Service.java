package com.a1stream.parts.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ020601BO;
import com.a1stream.domain.bo.parts.SPQ020601PrintBO;
import com.a1stream.domain.bo.parts.SPQ020602BO;
import com.a1stream.domain.form.parts.SPQ020601Form;
import com.a1stream.domain.form.parts.SPQ020602Form;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.PickingItemRepository;
import com.a1stream.domain.repository.PickingListRepository;

import jakarta.annotation.Resource;

/**
* 功能描述: Picking Instruction Inquiry
*
* mid2287
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
*/
@Service
public class SPQ0206Service {

    @Resource
    private PickingListRepository pickingListRepository;

    @Resource
    private PickingItemRepository pickingItemRepository;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    public Page<SPQ020601BO> getPickingInstructionList(SPQ020601Form form) {
        return pickingListRepository.getPickingInstructionList(form);
    }

    public List<SPQ020601BO> getPickingInstructionExportData(SPQ020601Form form) {
        return pickingListRepository.getPickingInstructionExportData(form);
    }

    public List<SPQ020602BO> getPickingInstructionDetailList(SPQ020602Form form) {
        return pickingListRepository.getPickingInstructionDetailList(form);
    }

    public List<SPQ020601BO> getPartsPickingListReport(String siteId, SPQ020601Form form) {

        return pickingListRepository.getPartsPickingListReport(siteId, form);
    }

    public SPQ020601PrintBO getPartsPickingListReportHeader(String siteId, SPQ020601Form form) {

        return pickingListRepository.getPartsPickingListReportHeader(siteId, form);
    }
}
