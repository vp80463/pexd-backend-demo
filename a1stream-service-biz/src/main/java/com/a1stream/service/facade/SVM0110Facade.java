package com.a1stream.service.facade;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.service.SVM011001BO;
import com.a1stream.domain.form.service.SVM011001Form;
import com.a1stream.domain.vo.ServicePdiItemVO;
import com.a1stream.domain.vo.ServicePdiVO;
import com.a1stream.service.service.SVM0110Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;


/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class SVM0110Facade {

    @Resource
    private SVM0110Service svm0110Service;

    public SVM011001Form getMotorcyclyInfoByFrameNo(SVM011001Form form) {

        SVM011001Form modelInfoForm = svm0110Service.getModelInfoByFrameNo(form);

        if(modelInfoForm != null) {

            SVM011001Form consumerInfoForm = svm0110Service.getConsumerInfoByFrameNo(modelInfoForm);

            if(consumerInfoForm != null) {

                modelInfoForm.setConsumerId(consumerInfoForm.getConsumerId());
                modelInfoForm.setConsumerCd(consumerInfoForm.getConsumerCd());
                modelInfoForm.setConsumerNm(consumerInfoForm.getConsumerNm());
            }
        }

        return modelInfoForm;
    }

    public List<SVM011001BO> getItemInfoList(SVM011001Form form) {

        List<SVM011001BO> pdiByProductCategoryIdInfoList = svm0110Service.getPdiInfoListByProductCategoryId(form);

        return pdiByProductCategoryIdInfoList.isEmpty() ? svm0110Service.getPdiInfoList(form)
                                                        : pdiByProductCategoryIdInfoList;
    }

    public void confirm(SVM011001Form form) {

        this.validateData(form);

        // 新增
        ServicePdiVO servicePdiVO = ServicePdiVO.create();
        servicePdiVO.setUpdateCount(0);
        servicePdiVO.setSiteId(form.getSiteId());
        servicePdiVO.setSerializedProductId(form.getSerializedProductId());
        servicePdiVO.setConsumerId(form.getConsumerId());
        servicePdiVO.setPdiPic(form.getMechanicNm());
        servicePdiVO.setPdiPicId(form.getMechanicId());
        servicePdiVO.setPdiDate(form.getPdiDate());
        servicePdiVO.setStartTime(form.getStartTime().replace(CommonConstants.CHAR_COLON, CommonConstants.CHAR_BLANK));
        servicePdiVO.setFinishTime(form.getFinishTime().replace(CommonConstants.CHAR_COLON, CommonConstants.CHAR_BLANK));
        servicePdiVO.setFacilityId(form.getPointId());

        // service_pdi_item 增
        List<SVM011001BO> tableDataList = form.getTableDataList();

        List<ServicePdiItemVO> servicePdiItemVOList = new ArrayList<>();

        for(SVM011001BO bo : tableDataList) {

            ServicePdiItemVO servicePdiItemVO = new ServicePdiItemVO();
            servicePdiItemVO.setSiteId(form.getSiteId());
            servicePdiItemVO.setServicePdiId(servicePdiVO.getServicePdiId());
            servicePdiItemVO.setDescription(bo.getItemCd() + bo.getItemNm());
            servicePdiItemVO.setResultStatus(bo.isCheckFlag() ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            servicePdiItemVO.setPdiSettingId(bo.getPdiSettingId());

            servicePdiItemVOList.add(servicePdiItemVO);
        }

        svm0110Service.updateConfirm(servicePdiVO, servicePdiItemVOList);
    }

    private void validateData(SVM011001Form form) {

        LocalTime finishTime = LocalTime.parse(form.getFinishTime());
        LocalTime startTime = LocalTime.parse(form.getStartTime());

        // 如果开始时间大于结束时间 则抛错：start time can not be later than finish time.
        if(finishTime.isBefore(startTime)) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00206", new String[] {CodedMessageUtils.getMessage("label.operationStartTime"), CodedMessageUtils.getMessage("label.operationFinishTime")}));
        }

        // 如果pointId为空，则抛错：point does not exist in Point Info.
        if(form.getPointId() == null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10237", new String[] {CodedMessageUtils.getMessage("label.point"), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        // 如果serializedProductId为空，则抛错：frame No. does not exist in serializedProduct.
        if(form.getSerializedProductId() == null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10237", new String[] {CodedMessageUtils.getMessage("label.frameNumber"), CodedMessageUtils.getMessage("label.tableSerializedProductInfo")}));
        }

        // service_pdi裔炯存在则报错.
        int rowCount = svm0110Service.getServicePdiRowCount(form);

        if(rowCount > 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00402"));
        }
    }
}