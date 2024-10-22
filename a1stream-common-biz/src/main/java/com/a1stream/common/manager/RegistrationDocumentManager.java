package com.a1stream.common.manager;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.RegistrationDocument;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.GenderType;
import com.a1stream.common.constants.PJConstants.RegistrationDocumentFeatrueCategory;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmGeorgaphyRepository;
import com.a1stream.domain.repository.ConsumerPrivateDetailRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Component
public class RegistrationDocumentManager {

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private CmmConsumerRepository cmmConsummerRepository;

    @Resource
    private CmmGeorgaphyRepository cmmGeorgaphyRepository;

    @Resource
    private ConsumerPrivateDetailRepository consumerPrivateDetailRepository;

    @Resource
    private BatteryRepository batteryRepository;

    //TODO Call APi 给SW 送数据result，需要调用IFS，待补充
    public void sendToSW(CmmRegistrationDocumentVO cmmRegistrationDocumentVO, String type) {
        
        if (InterfCode.XM03_INTERF_O_SV_REG_DOC.equals(type)) {
            this.sendRegistrationDocument(cmmRegistrationDocumentVO);
        } else {
            this.sendRegistrationDocumentForBattery(cmmRegistrationDocumentVO);
        }
    }

    public void sendRegistrationDocument(CmmRegistrationDocumentVO cmmRegistrationDocumentVO) {

        SerializedProductVO serializedProductVO = BeanMapUtils.mapTo(serializedProductRepository.findBySerializedProductId(cmmRegistrationDocumentVO.getSerializedProductId()), SerializedProductVO.class);
        MstFacilityVO mstFacilityVO = BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(cmmRegistrationDocumentVO.getFacilityId()), MstFacilityVO.class);
        SalesOrderVO salesOrderVO = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(cmmRegistrationDocumentVO.getSalesOrderId()), SalesOrderVO.class);
        CmmConsumerVO cmmConsumerVO = BeanMapUtils.mapTo(cmmConsummerRepository.findByConsumerId(cmmRegistrationDocumentVO.getConsumerId()), CmmConsumerVO.class);
        CmmGeorgaphyVO cmmGeographyVO = BeanMapUtils.mapTo(cmmGeorgaphyRepository.findByGeographyId(cmmConsumerVO.getProvinceGeographyId()), CmmGeorgaphyVO.class);
        ConsumerPrivateDetailVO consumerPrivateDetailVO = BeanMapUtils.mapTo(consumerPrivateDetailRepository.findFirstByConsumerIdAndSiteId(cmmRegistrationDocumentVO.getConsumerId(), cmmRegistrationDocumentVO.getSiteId()), ConsumerPrivateDetailVO.class);

        RegistrationDocument registrationDocument = new RegistrationDocument();
        registrationDocument.setFrameNo(serializedProductVO.getFrameNo());
        registrationDocument.setRegistrationDealerCode(cmmRegistrationDocumentVO.getSiteId());
        registrationDocument.setSalesDate(salesOrderVO.getOrderDate());
        registrationDocument.setDateType("REGIST");
        registrationDocument.setUseType(cmmRegistrationDocumentVO.getUseType());
        if (RegistrationDocumentFeatrueCategory.USETYPE001.getCodeDbid().equals(cmmRegistrationDocumentVO.getUseType())) {
            registrationDocument.setOwnerType(RegistrationDocumentFeatrueCategory.OWNERTYPE001.getCodeData1());
            registrationDocument.setOwnerNameFirst(cmmConsumerVO.getFirstNm());
            registrationDocument.setOwnerNameMiddle(cmmConsumerVO.getMiddleNm());
            registrationDocument.setOwnerNameLast(cmmConsumerVO.getLastNm());
        } else if (RegistrationDocumentFeatrueCategory.USETYPE002.getCodeDbid().equals(cmmRegistrationDocumentVO.getUseType())) {
            registrationDocument.setOwnerType(RegistrationDocumentFeatrueCategory.OWNERTYPE002.getCodeData1());
            registrationDocument.setBusinessNameFirst(cmmConsumerVO.getFirstNm());
            registrationDocument.setBusinessNameMiddle(cmmConsumerVO.getMiddleNm());
            registrationDocument.setBusinessNameLast(cmmConsumerVO.getLastNm());
        }
        registrationDocument.setAddress1(cmmConsumerVO.getAddress());
        registrationDocument.setAddress2(cmmConsumerVO.getAddress2());
        registrationDocument.setProvinceCode(cmmConsumerVO.getProvinceGeographyId());
        registrationDocument.setProvinceName(cmmGeographyVO.getGeographyNm());
        registrationDocument.setCity(cmmConsumerVO.getCityGeographyId());
        registrationDocument.setTelephoneNumber(consumerPrivateDetailVO.getMobilePhone());
        registrationDocument.setCellPhoneNumber(consumerPrivateDetailVO.getMobilePhone2());
        registrationDocument.setFaxNumber(cmmConsumerVO.getFaxNo());
        registrationDocument.setSex(GenderType.MALE.getCodeDbid().equals(cmmConsumerVO.getGender()) ? "L" : "M");
        registrationDocument.setBirthday(cmmConsumerVO.getBirthYear() + cmmConsumerVO.getBirthDate());
        registrationDocument.setOccupationTp(cmmConsumerVO.getOccupation());
        registrationDocument.setEmailPrimary(cmmConsumerVO.getEmail());
        registrationDocument.setEmailSecondary(cmmConsumerVO.getEmail2());
        registrationDocument.setComment(cmmConsumerVO.getComment());
        registrationDocument.setRegistrationPointCode(mstFacilityVO.getFacilityCd());
        registrationDocument.setCategoryType("MC");
        if (CommonConstants.CHAR_Y.equals(serializedProductVO.getEvFlag())) {
            BatteryVO batteryVO1 = this.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != batteryVO1) {
                registrationDocument.setBatteryId1(batteryVO1.getBatteryNo());
                registrationDocument.setBatteryCode1(batteryVO1.getBatteryCd());
            }

            BatteryVO batteryVO2 = this.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != batteryVO2) {
                registrationDocument.setBatteryId1(batteryVO2.getBatteryNo());
                registrationDocument.setBatteryCode1(batteryVO2.getBatteryCd());
            }
            registrationDocument.setCategoryType("EV");
        }
        registrationDocument.setPdiDate(salesOrderVO.getShipDate());
        registrationDocument.setJustificationComment("A1-STREAM Modification");
    }

    public void sendRegistrationDocumentForBattery(CmmRegistrationDocumentVO cmmRegistrationDocumentVO) {
        
        SerializedProductVO serializedProductVO = BeanMapUtils.mapTo(serializedProductRepository.findBySerializedProductId(cmmRegistrationDocumentVO.getSerializedProductId()), SerializedProductVO.class);
        MstFacilityVO mstFacilityVO = BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(cmmRegistrationDocumentVO.getFacilityId()), MstFacilityVO.class);
        SalesOrderVO salesOrderVO = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(cmmRegistrationDocumentVO.getSalesOrderId()), SalesOrderVO.class);
        CmmConsumerVO cmmConsumerVO = BeanMapUtils.mapTo(cmmConsummerRepository.findByConsumerId(cmmRegistrationDocumentVO.getConsumerId()), CmmConsumerVO.class);
        CmmGeorgaphyVO cmmGeographyVO = BeanMapUtils.mapTo(cmmGeorgaphyRepository.findByGeographyId(cmmConsumerVO.getProvinceGeographyId()), CmmGeorgaphyVO.class);
        ConsumerPrivateDetailVO consumerPrivateDetailVO = BeanMapUtils.mapTo(consumerPrivateDetailRepository.findFirstByConsumerIdAndSiteId(cmmRegistrationDocumentVO.getConsumerId(), cmmRegistrationDocumentVO.getSiteId()), ConsumerPrivateDetailVO.class);

        RegistrationDocument registrationDocument = new RegistrationDocument();
        registrationDocument.setRegistrationDealerCode(cmmRegistrationDocumentVO.getSiteId());
        registrationDocument.setSalesDate(salesOrderVO.getOrderDate());
        registrationDocument.setDateType("REGIST");
        registrationDocument.setUseType(RegistrationDocumentFeatrueCategory.USETYPE001.getCodeData1());
        registrationDocument.setOwnerType(RegistrationDocumentFeatrueCategory.OWNERTYPE001.getCodeData1());
        registrationDocument.setOwnerNameFirst(cmmConsumerVO.getFirstNm());
        registrationDocument.setOwnerNameMiddle(cmmConsumerVO.getMiddleNm());
        registrationDocument.setOwnerNameLast(cmmConsumerVO.getLastNm());
        registrationDocument.setAddress1(cmmConsumerVO.getAddress());
        registrationDocument.setAddress2(cmmConsumerVO.getAddress2());
        registrationDocument.setProvinceCode(cmmConsumerVO.getProvinceGeographyId());
        registrationDocument.setProvinceName(cmmGeographyVO.getGeographyNm());
        registrationDocument.setCity(cmmConsumerVO.getCityGeographyId());
        registrationDocument.setTelephoneNumber(consumerPrivateDetailVO.getMobilePhone());
        registrationDocument.setCellPhoneNumber(consumerPrivateDetailVO.getMobilePhone2());
        registrationDocument.setFaxNumber(cmmConsumerVO.getFaxNo());
        registrationDocument.setSex(GenderType.MALE.getCodeDbid().equals(cmmConsumerVO.getGender()) ? "L" : "M");
        registrationDocument.setBirthday(cmmConsumerVO.getBirthYear() + cmmConsumerVO.getBirthDate());
        registrationDocument.setOccupationTp(cmmConsumerVO.getOccupation());
        registrationDocument.setEmailPrimary(cmmConsumerVO.getEmail());
        registrationDocument.setEmailSecondary(cmmConsumerVO.getEmail2());
        registrationDocument.setComment(cmmConsumerVO.getComment());
        registrationDocument.setRegistrationPointCode(mstFacilityVO.getFacilityCd());
        BatteryVO batteryVO1 = this.getBatteryVO(cmmRegistrationDocumentVO.getBatteryId());
        if (null != batteryVO1) {
            registrationDocument.setBatteryId1(batteryVO1.getBatteryNo());
            registrationDocument.setBatteryCode1(batteryVO1.getBatteryCd());
        }
        registrationDocument.setCategoryType("BATTERY");
        registrationDocument.setPdiDate(salesOrderVO.getShipDate());
        registrationDocument.setJustificationComment("A1-STREAM Modification");
    }

    public BatteryVO getBatteryVO(Long serializedProductId, String positoinSign) {

        return BeanMapUtils.mapTo(batteryRepository.findBySerializedProductIdAndPositionSign(serializedProductId, positoinSign), BatteryVO.class);
    }

    public BatteryVO getBatteryVO(Long batteryId) {

        return BeanMapUtils.mapTo(batteryRepository.findByBatteryId(batteryId), BatteryVO.class);
    }
}
