package com.a1stream.common.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.bo.CreateRegistrationDocumentBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.entity.CmmRegistrationDocument;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Service
public class CmmSerializedProductManager {

    @Resource
    private CmmRegistrationDocumentRepository cmmRegistrationDocumentRepository;

    /**
     * 创建注册文档。
     *
     * @param salesOrderVO 销售订单信息。
     * @param model 创建注册文档的业务对象
     * @throws PJCustomException 如果创建过程中发生异常。
     */
    public CreateRegistrationDocumentBO doCreateRegistrationDocument(SalesOrderVO salesOrderVO, CreateRegistrationDocumentBO model) {

        try {
            CmmRegistrationDocumentVO cmmRegistrationDocumentVO = this.createRegistrationDocumentVO(model, salesOrderVO);
            CmmRegistrationDocument cmmRegistrationDocument = BeanMapUtils.mapTo(cmmRegistrationDocumentVO, CmmRegistrationDocument.class);
            cmmRegistrationDocumentRepository.save(cmmRegistrationDocument);
            model.setCmmRegistrationDocument(BeanMapUtils.mapTo(cmmRegistrationDocument, CmmRegistrationDocumentVO.class));

            List<CmmRegistrationDocumentVO> batteriesVOList = new ArrayList<>();
            if (model.getBatteryId1() != null){
                batteriesVOList.add(this.createBatteryDocument(model, salesOrderVO, model.getBatteryId1()));
            }
            if (model.getBatteryId2() != null){
                batteriesVOList.add(this.createBatteryDocument(model, salesOrderVO, model.getBatteryId2()));
            }
            if (!batteriesVOList.isEmpty()){
                List<CmmRegistrationDocument> batteriesToSaveList = BeanMapUtils.mapListTo(batteriesVOList, CmmRegistrationDocument.class);
                cmmRegistrationDocumentRepository.saveInBatch(batteriesToSaveList);
                model.setBatteriesToSaveList(BeanMapUtils.mapListTo(batteriesToSaveList, CmmRegistrationDocumentVO.class));
            }
            
            return model;
        } catch (Exception e){
            throw new PJCustomException(BaseResult.ERROR_MESSAGE);
        }
    }

    private CmmRegistrationDocumentVO createRegistrationDocumentVO(CreateRegistrationDocumentBO model, SalesOrderVO salesOrderVO) {

        CmmRegistrationDocumentVO cmmRegistrationDocumentVO = CmmRegistrationDocumentVO.create();
        cmmRegistrationDocumentVO.setRegistrationDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        cmmRegistrationDocumentVO.setRegistrationTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M));
        cmmRegistrationDocumentVO.setFacilityId(model.getPointId());
        cmmRegistrationDocumentVO.setConsumerId(model.getCustomerId());
        cmmRegistrationDocumentVO.setSalesOrderId(salesOrderVO.getSalesOrderId());
        cmmRegistrationDocumentVO.setSerializedProductId(model.getSerializedProductId());
        cmmRegistrationDocumentVO.setUseType(model.getUseType());
        cmmRegistrationDocumentVO.setOwnerType(model.getOwnerType());
        cmmRegistrationDocumentVO.setPurchaseType(model.getPurchaseType());
        cmmRegistrationDocumentVO.setPsvBrandNm(model.getPreviousBikeBrandName());
        cmmRegistrationDocumentVO.setPBikeNm(model.getPreviousBikeName());
        cmmRegistrationDocumentVO.setMtAtId(model.getPreviousBikeMtat());
        cmmRegistrationDocumentVO.setFamilyNum(null == model.getNumberOfFamilyMember() ? CommonConstants.INTEGER_ZERO : model.getNumberOfFamilyMember());
        cmmRegistrationDocumentVO.setNumBike(null == model.getNumberOfBikesInFamily() ? CommonConstants.INTEGER_ZERO : model.getNumberOfBikesInFamily());
        cmmRegistrationDocumentVO.setSiteId(salesOrderVO.getSiteId());
        return cmmRegistrationDocumentVO;
    }

    private CmmRegistrationDocumentVO createBatteryDocument(CreateRegistrationDocumentBO model, SalesOrderVO salesOrderVO, Long batteryId) {

        CmmRegistrationDocumentVO batteryDocumentVO = CmmRegistrationDocumentVO.create();
        batteryDocumentVO.setRegistrationDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        batteryDocumentVO.setRegistrationTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M));
        batteryDocumentVO.setFacilityId(model.getPointId());
        batteryDocumentVO.setConsumerId(model.getCustomerId());
        batteryDocumentVO.setSalesOrderId(salesOrderVO.getSalesOrderId());
        batteryDocumentVO.setBatteryId(batteryId);
        batteryDocumentVO.setSiteId(salesOrderVO.getSiteId());

        return batteryDocumentVO;
    }
}
