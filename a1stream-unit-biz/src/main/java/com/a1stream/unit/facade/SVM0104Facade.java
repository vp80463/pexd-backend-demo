package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.ConsumerSerialProRelationType;
import com.a1stream.common.constants.PJConstants.GenderType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SVM010401BO;
import com.a1stream.domain.bo.unit.SVM010402BO;
import com.a1stream.domain.bo.unit.SVM010402ConsumerInfoBO;
import com.a1stream.domain.bo.unit.SVM010402ServiceHistoryBO;
import com.a1stream.domain.bo.unit.SVM010402TransactionHistoryBO;
import com.a1stream.domain.form.unit.SVM010401Form;
import com.a1stream.domain.form.unit.SVM010402Form;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConsumerSerialProRelationVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.unit.service.SVM0104Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Motorcycle Inquiry
*
* mid2287
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/19   Wang Nan      New
*/
@Component
public class SVM0104Facade {

    @Resource
    private SVM0104Service svm0104Service;

    @Resource
    private ConsumerLogic consumerLogic;

    @Resource
    private HelperFacade helperFacade;

    private static final List<String> PURCHASE_LIST = Arrays.asList(InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid(),
                                                                    InventoryTransactionType.PURCHASERETURNOUT.getCodeDbid());

    private static final List<String> TRANSFER_LIST = Arrays.asList(InventoryTransactionType.TRANSFEROUT.getCodeDbid(),
                                                                    InventoryTransactionType.TRANSFERIN.getCodeDbid());

    private static final String STOCK_CODE_ID       = "S033";
    private static final String QUALITY_CODE_ID     = "S039";
    private static final String SALES_CODE_ID       = "S040";

    private static final String SERIALPLATENO_STR   = "SERIALPLATENO";

    /**
     * 一览检索
     */
    public Page<SVM010401BO> getMotorcycleList(SVM010401Form form, PJUserDetails uc) {

        validateBeforeRetrieve(form);
        String siteId = uc.getDealerCode();
        //若consumer/mobilePhone不为空, 则先进行人车关系查询
        if (!Nulls.isNull(form.getConsumerId()) || StringUtils.isNotBlank(form.getMobilePhone())) {
            List<Long> serialProductIds = svm0104Service.getSerialProductIds(form.getConsumerId(), form.getMobilePhone(), siteId);
            //去除list中的null值
            serialProductIds.removeIf(Objects::isNull);
            form.setSerializedProductIdList(serialProductIds);
        }
        //先进行local查询
        Page<SVM010401BO> returnList = svm0104Service.getLocalMcData(form, siteId);
        //当条件部输入frameNo，plateNo或者BatteryId时，若local查询无数据, 则进行common查询
        if (returnList.isEmpty()) {
            if (!StringUtils.isEmpty(form.getFrameNo()) || !StringUtils.isEmpty(form.getPlateNo()) || !StringUtils.isEmpty(form.getBatteryId())) {
                returnList = svm0104Service.getCommonMcData(form, siteId);
            }
        }
        if (!returnList.isEmpty()) {
            //设置salseStatus(cmm_serialized_product的salse_status) 和 stockStatus(serialized_product的stock_status)
            setValue2Status(returnList, siteId);
            //设置serviceFlag
            setValue2ServiceFlag(returnList, siteId);
            //codeDbid -> codeData1
            setValue2Desc(returnList);
        }

        return returnList;
    }

    /**
     * 明细检索
     */
    public SVM010402BO getMotorcycleInfo(SVM010402Form form, PJUserDetails uc) {

        Long cmmSerialProId = form.getCmmSerializedProductId();
        String siteId = uc.getDealerCode();

        //1.获取basicInfo
        SVM010402BO basicInfo = getMcBasicInfo(cmmSerialProId, siteId);
        //2.获取ConsumerInfo
        basicInfo.setConsumerInfoList(getMcConsumerData(cmmSerialProId, siteId));
        //3.获取TransactionHistory
        basicInfo.setTransactionHistoryList(getMcTransHistData(cmmSerialProId, siteId));
        //4.获取ServiceHistory
        basicInfo.setServiceHistoryList(getMcServiceHistData(cmmSerialProId, siteId));

        return basicInfo;
    }

    /**
     * 侧边栏检索
     */
    public SVM010402BO getConsumerInfo(SVM010402Form form, PJUserDetails uc) {

        String siteId = uc.getDealerCode();
        Long cmmSerialProductId = form.getCmmSerializedProductId();
        SVM010402BO consumerInfo = svm0104Service.getOwnerConsumerByMc(cmmSerialProductId);
        if (consumerInfo == null) {
            consumerInfo = new SVM010402BO();
        }
        if (form.getConsumerId() != null) {
            SVM010402ConsumerInfoBO consumerBasicInfo = svm0104Service.getMcConsumerById(cmmSerialProductId, form.getConsumerId(), siteId);
            consumerInfo.setConsumerBasicInfo(consumerBasicInfo);
        }

        return consumerInfo;
    }

    /**
     * 更新或新增ConsumerInfo
     */
    public void saveConsumerInfo(SVM010402Form form, String siteId) {

        CmmConsumerVO cmmConsumer = buildCmmConsumer(form);
        ConsumerPrivateDetailVO consumerPrivateDetail = buildConsumerPrivateDetail(form, siteId);
        List<CmmConsumerSerialProRelationVO> consumerAndMcRelations = buildConsumerAndMcRelations(form);

        svm0104Service.updateConsumerInfo(cmmConsumer, consumerPrivateDetail, consumerAndMcRelations);
    }

    /**
     * 保存
     */
    public void saveMotorcycleInfo(SVM010402Form form, PJUserDetails uc) {

        validateBeforeSave(form);
        String siteId = uc.getDealerCode();
        // 1.更新车牌号
        CmmSerializedProductVO cmmSerialProduct = svm0104Service.getCmmSerialProductById(form.getCmmSerializedProductId());
        SerializedProductVO serialProduct = null;
        if (!Nulls.isNull(cmmSerialProduct)) {
            cmmSerialProduct.setPlateNo(form.getPlateNo());
            serialProduct = svm0104Service.getSerializedProduct(cmmSerialProduct.getSerializedProductId(), siteId);
            if (!Nulls.isNull(serialProduct)) {
                serialProduct.setPlateNo(form.getPlateNo());
            }
        }
        // 2.若ev_flag='Y' & 电池校验通过则更新电池相关信息
        List<CmmBatteryVO> updCmmBatteryList = new ArrayList<>();
        List<BatteryVO> updBatteryList = new ArrayList<>();
        if (StringUtils.equals(CommonConstants.CHAR_Y, form.getEvFlag())) {
            String sysDate = ComUtil.nowLocalDate();
            String maxDate = CommonConstants.MAX_DATE;

            Set<String> batteryNos = new HashSet<>();
            batteryNos.add(form.getBatteryId1());
            batteryNos.add(form.getBatteryId2());
            batteryNos.add(form.getOldBatteryId1());
            batteryNos.add(form.getOldBatteryId2());

            Map<String, BatteryVO> batteryMap = svm0104Service.findBatteryNoMap(batteryNos);
            Map<String, CmmBatteryVO> CmmBatteryMap = svm0104Service.findCmmBatteryNoMap(batteryNos);
            // 电池相关校验
            validationBattery(form.getBatteryId1(), form.getBatteryId2(), batteryMap, CmmBatteryMap);
            // 2.1更新旧电池battery1
            updateOldBattery(updCmmBatteryList, updBatteryList, CmmBatteryMap, batteryMap, form.getOldBatteryId1(), sysDate);
            // 2.2更新旧电池battery2
            updateOldBattery(updCmmBatteryList, updBatteryList, CmmBatteryMap, batteryMap, form.getOldBatteryId2(), sysDate);

            // 2.3更新新电池battery1
            updateNewBattery(updCmmBatteryList, updBatteryList, batteryMap, CmmBatteryMap, form, BatteryType.TYPE1.getCodeDbid(), sysDate, maxDate);
            // 2.4更新新电池battery2
            updateNewBattery(updCmmBatteryList, updBatteryList, batteryMap, CmmBatteryMap, form, BatteryType.TYPE2.getCodeDbid(), sysDate, maxDate);
        }
        // TODO 同步数据sendToINSPIRE
        svm0104Service.maintainMcData(cmmSerialProduct, serialProduct, updCmmBatteryList, updBatteryList);
    }

    /**
     * 一览检索前check
     */
    private void validateBeforeRetrieve(SVM010401Form form) {
        // model存在性check
        if (StringUtils.isNotBlank(form.getModel()) && Nulls.isNull(form.getModelId())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] { ComUtil.t("label.model"), form.getModel(), ComUtil.t("label.productInformation") }));
        }

        // mobilePhone固定10位
        if (StringUtils.isNotBlank(form.getMobilePhone()) && form.getMobilePhone().length() != 10) {
            throw new BusinessCodedException(ComUtil.t("M.E.10322", new String[] { ComUtil.t("label.mobilephone"), "10" }));
        }

        // batteryId固定17位
        if (StringUtils.isNotBlank(form.getBatteryId()) && form.getBatteryId().length() != 17) {
            throw new BusinessCodedException(ComUtil.t("M.E.10322", new String[] { ComUtil.t("label.batteryId"), "17" }));
        }
    }

    /**
     * 设置salseStatus和stockStatus
     */
    private void setValue2Status(Page<SVM010401BO> list, String siteId) {

        Set<Long> cmmSerialProIds = list.stream().map(SVM010401BO::getCmmSerializedProductId).collect(Collectors.toSet());

        Map<Long, String> salesStatusMap = svm0104Service.getProdAndSalesStsMap(cmmSerialProIds);
        //统一设置为cmm_serialized_product的sales_status, 没有则为空
        list.forEach(bo -> {
            String salesStatus = salesStatusMap.getOrDefault(bo.getCmmSerializedProductId(), StringUtils.EMPTY);
            bo.setSalesStatus(salesStatus);
        });

        Map<Long, String> stockStatusMap = svm0104Service.getProdAndStockStsMap(cmmSerialProIds, siteId);
        //统一设置为serialized_product的stock_status, 没有则为空
        list.forEach(bo -> {
            String stockStatus = stockStatusMap.getOrDefault(bo.getCmmSerializedProductId(), StringUtils.EMPTY);
            bo.setStockStatus(stockStatus);
        });
    }

    /**
     * 设置serviceFlag
     */
    private void setValue2ServiceFlag(Page<SVM010401BO> list, String siteId) {

        List<String> frameNoList = list.stream().map(SVM010401BO::getFrameNo).toList();
        //siteId和frameNo在service_order查询得到serviceOrderVOList
        List<ServiceOrderVO> serviceOrderVOList = svm0104Service.getServiceOrderVOList(frameNoList);

        if (!serviceOrderVOList.isEmpty()) {
            List<String> serviceOrderFrameNoList = serviceOrderVOList.stream().map(ServiceOrderVO::getFrameNo).toList();
            //若list存在于service_order表中将serviceFlag为Y, 否则为N
            list.forEach(item -> {
                if (serviceOrderFrameNoList.contains(item.getFrameNo())) {
                    item.setServiceFlag(CommonConstants.CHAR_Y);
                } else {
                    item.setServiceFlag(CommonConstants.CHAR_N);
                }
            });
        } else {
            list.forEach(item -> item.setServiceFlag(CommonConstants.CHAR_N));
        }
        //从cmm_serialized_product中查询出的数据将serviceFlag设置为N
        list.stream()
            .filter(item -> StringUtils.equals(CommonConstants.CHAR_S, item.getP()))
            .forEach(item -> item.setServiceFlag(CommonConstants.CHAR_N));
    }

    /**
     * codeDbid -> codeData1
     */
    private void setValue2Desc(Page<SVM010401BO> returnList) {
        //获取codeMap
        Map<String, String> mstCodeMap = helperFacade.getMstCodeInfoMap(STOCK_CODE_ID, QUALITY_CODE_ID, SALES_CODE_ID);
        //codeDbid -> codeData1
        returnList.forEach(bo -> {
            bo.setStockStatus(mstCodeMap.get(bo.getStockStatus()));
            bo.setSalesStatus(mstCodeMap.get(bo.getSalesStatus()));
            bo.setQualityStatus(mstCodeMap.get(bo.getQualityStatus()));
        });
    }

    /**
     * 检索BasicInfo
     */
    private SVM010402BO getMcBasicInfo(Long cmmSerialProId, String siteId) {

        SVM010402BO basicInfo = svm0104Service.getMcBasicInfo(cmmSerialProId);
        if (!Nulls.isNull(basicInfo)) {
            // 设置stockStatus(serialized_product)
            SerializedProductVO serializedProVO = svm0104Service.getSerializedProduct(cmmSerialProId , siteId);
            if (!Nulls.isNull(serializedProVO)) {
                basicInfo.setStockStatus(serializedProVO.getStockStatus());
            }
            //获取codeMap
            Map<String, String> mstCodeMap = helperFacade.getMstCodeInfoMap(STOCK_CODE_ID, QUALITY_CODE_ID, SALES_CODE_ID);

            basicInfo.setStockStatusNm(mstCodeMap.get(basicInfo.getStockStatus()));
            basicInfo.setSalesStatusNm(mstCodeMap.get(basicInfo.getSalesStatus()));
            basicInfo.setQualityStatusNm(mstCodeMap.get(basicInfo.getQualityStatus()));
        }

        return basicInfo;
    }

    /**
     * 检索ConsumerInfo
     */
    private List<SVM010402ConsumerInfoBO> getMcConsumerData(Long cmmSerialProId, String siteId) {

        List<SVM010402ConsumerInfoBO> consumerData = svm0104Service.getMcConsumerData(cmmSerialProId, siteId);
        Map<String, String> mstCodeMap = helperFacade.getMstCodeInfoMap(ConsumerSerialProRelationType.CODE_ID, GenderType.CODE_ID);

        consumerData.forEach(bo -> {
            bo.setRelationType(mstCodeMap.get(bo.getRelationType()));
            bo.setGender(mstCodeMap.get(bo.getGender()));
        });

        return consumerData;
    }

    /**
     * 检索ServiceHistory
     */
    private List<SVM010402ServiceHistoryBO> getMcServiceHistData(Long cmmSerialProId, String siteId) {

        List<SVM010402ServiceHistoryBO> serviceHistData = svm0104Service.getMcServiceHistData(cmmSerialProId);

        //若serviceHistoryList的siteId != uc.siteId, 则orderNo为空
        serviceHistData.forEach(bo -> {
            if (!StringUtils.equals(bo.getSiteId(), siteId)) {
                bo.setOrderNo(StringUtils.EMPTY);
            }
        });

        return serviceHistData;
    }

    /**
     * 检索TransactionHistory
     */
    private List<SVM010402TransactionHistoryBO> getMcTransHistData(Long cmmSerialProId, String siteId) {

        List<SVM010402TransactionHistoryBO> traHisList = svm0104Service.getMcTransHistData(cmmSerialProId, siteId);

        if (!traHisList.isEmpty()) {

            Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(InventoryTransactionType.CODE_ID);

            //获取ConsumerMap
            List<Long> consumerIdList = traHisList.stream().filter(item -> !Nulls.isNull(item.getToConsumerId())).map(SVM010402TransactionHistoryBO::getToConsumerId).toList();
            Map<Long, String> consumerMap = svm0104Service.getCmmConsumerMap(consumerIdList);

            //获取MstOrganizationMap(to)
            List<Long> toPartyIdList = traHisList.stream().filter(item -> !Nulls.isNull(item.getToPartyId())).map(SVM010402TransactionHistoryBO::getToPartyId).toList();
            //获取MstOrganizationMap(from)
            List<Long> fromPartyIdList = traHisList.stream().filter(item -> !Nulls.isNull(item.getFromPartyId())).map(SVM010402TransactionHistoryBO::getFromPartyId).toList();

            Set<Long> orgIds = new HashSet<>();
            orgIds.addAll(toPartyIdList);
            orgIds.addAll(fromPartyIdList);
            Map<Long, String> mstOrganizationMap = svm0104Service.getMstOrganizationVOList(orgIds);

            //获取MstFacilityMap(to)
            List<Long> toFaciclityIdList = traHisList.stream().filter(item -> !Nulls.isNull(item.getToFacilityId())).map(SVM010402TransactionHistoryBO::getToFacilityId).toList();
            //获取MstFacilityMap(from)
            List<Long> fromFaciclityIdList = traHisList.stream().filter(item -> !Nulls.isNull(item.getFromFacilityId())).map(SVM010402TransactionHistoryBO::getFromFacilityId).toList();

            Set<Long> facilityIds = new HashSet<>();
            facilityIds.addAll(toFaciclityIdList);
            facilityIds.addAll(fromFaciclityIdList);
            Map<Long, String> mstFacilityMap = svm0104Service.getMstFacilityVOList(facilityIds);

            for (SVM010402TransactionHistoryBO tranHisBO : traHisList) {

                //若toConsumerId != null
                if (!Nulls.isNull(tranHisBO.getToConsumerId())) {
                    //设置To为toConsumerId在cmm_consumer中对应数据的consumer_full_name
                    tranHisBO.setTo(consumerMap.get(tranHisBO.getToConsumerId()));
                } else {
                    //设置To为toPartyId在mst_organization中对应数据的organization_cd
                    tranHisBO.setTo(mstOrganizationMap.get(tranHisBO.getToPartyId()));
                }

                //transactionTypeId == S027PURCHASESTOCKIN / S027PURCHASERETURNOUT
                if (PURCHASE_LIST.contains(tranHisBO.getTransactionTypeId())) {
                    // 设置from为fromPartyInfo在mst_organization中对应数据的organization_cd
                    tranHisBO.setFrom(mstOrganizationMap.get(tranHisBO.getFromPartyId()));
                    //设置to为toPartyId在mst_organization中对应数据的organization_cd
                    tranHisBO.setTo(mstOrganizationMap.get(tranHisBO.getToPartyId()));
                }

                //transactionTypeId == S027TRANSFEROUT / S027TRANSFERIN
                if (TRANSFER_LIST.contains(tranHisBO.getTransactionTypeId())) {
                    // 设置from为fromFacilityId在mst_facility中对应数据的facility_cd
                    tranHisBO.setFrom(mstFacilityMap.get(tranHisBO.getFromFacilityId()));
                    // 设置to为foFacilityId在mst_facility中对应数据的facility_cd
                    tranHisBO.setTo(mstFacilityMap.get(tranHisBO.getToFacilityId()));
                }

                //codeDbid -> codeData1
                tranHisBO.setTransactionTypeId(codeMap.get(tranHisBO.getTransactionTypeId()));
            }
        }

        return traHisList;
    }

    /**
     * 更新CmmConsumer
     */
    private CmmConsumerVO buildCmmConsumer(SVM010402Form form) {

        CmmConsumerVO cmmConsumer = null;

        if (!Nulls.isNull(form.getConsumerId())) {

            cmmConsumer = svm0104Service.getCmmConsumer(form.getConsumerId());

            if (!Nulls.isNull(cmmConsumer)) {

                String consumerFullNm = consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm());
                String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());

                cmmConsumer.setFirstNm(form.getFirstNm());
                cmmConsumer.setMiddleNm(form.getMiddleNm());
                cmmConsumer.setLastNm(form.getLastNm());
                cmmConsumer.setConsumerFullNm(consumerFullNm);
                cmmConsumer.setConsumerRetrieve(consumerRetrieve);
                cmmConsumer.setGender(form.getGender());
                cmmConsumer.setProvinceGeographyId(form.getProvince());
                cmmConsumer.setCityGeographyId(form.getCity());
                cmmConsumer.setTelephone(form.getTelephone());
                cmmConsumer.setAddress(form.getAddress1());
                cmmConsumer.setAddress2(form.getAddress2());

            }
        }

        return cmmConsumer;
    }

    /**
     * 更新ConsumerPrivateDetail
     */
    private ConsumerPrivateDetailVO buildConsumerPrivateDetail(SVM010402Form form, String siteId) {

        ConsumerPrivateDetailVO consumerPrivateDetail = null;

        Long consumerId = form.getConsumerId();
        if (!Objects.isNull(consumerId)) {

            consumerPrivateDetail = svm0104Service.findConsumerPrivateDetail(consumerId, siteId);

            if (Objects.isNull(consumerPrivateDetail)) {
                consumerPrivateDetail = new ConsumerPrivateDetailVO();
                consumerPrivateDetail.setSiteId(siteId);
                consumerPrivateDetail.setConsumerId(consumerId);
            }

            String consumerFullNm = consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm());
            String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());

            consumerPrivateDetail.setFirstNm(form.getFirstNm());
            consumerPrivateDetail.setMiddleNm(form.getMiddleNm());
            consumerPrivateDetail.setLastNm(form.getLastNm());
            consumerPrivateDetail.setConsumerFullNm(consumerFullNm);
            consumerPrivateDetail.setConsumerRetrieve(consumerRetrieve);
            consumerPrivateDetail.setMobilePhone(form.getMobilePhone());
        }

        return consumerPrivateDetail;
    }

    /**
     * 更新或新增人车关系
     */
    private List<CmmConsumerSerialProRelationVO> buildConsumerAndMcRelations(SVM010402Form form) {

        Long consumerId = form.getConsumerId();
        String relationTypeId = form.getRelationType();

        List<CmmConsumerSerialProRelationVO> cmmConsumerAndMcRelaList = new ArrayList<>();

        // 变更Relation Type时：
        CmmConsumerSerialProRelationVO ownerRelation = svm0104Service.findMcOwnerConsumer(form.getCmmSerializedProductId());
        // User改为Owner时, 需要将原有的owner更新成user
        if (StringUtils.equals(ConsumerSerialProRelationType.OWNER.getCodeDbid(), relationTypeId)) {
            // 车辆有Owner, 且不是当前consumer
            if (!Objects.isNull(ownerRelation) && !ownerRelation.getConsumerId().equals(consumerId)) {
                // 改为user
                ownerRelation.setConsumerSerializedProductRelationTypeId(ConsumerSerialProRelationType.USER.getCodeDbid());
                ownerRelation.setOwnerFlag(CommonConstants.CHAR_N);

                cmmConsumerAndMcRelaList.add(ownerRelation);
            }
        }
        // Owner改为User时，Owner是必须存在的
        else if (StringUtils.equals(ConsumerSerialProRelationType.USER.getCodeDbid(), relationTypeId)) {
            if (!Objects.isNull(ownerRelation) && ownerRelation.getConsumerId().equals(consumerId)) {
                throw new BusinessCodedException(ComUtil.t("M.E.10326", new String[] { ComUtil.t("label.owner") }));
            }
        }

        // 人车关系
        CmmConsumerSerialProRelationVO cmmConsumerAndMcRela = buildConsumerAndMcRelation(form);
        cmmConsumerAndMcRelaList.add(cmmConsumerAndMcRela);

        return cmmConsumerAndMcRelaList;
    }

    /**
     * 创建人车关系
     */
    private CmmConsumerSerialProRelationVO buildConsumerAndMcRelation(SVM010402Form form) {

        Long consumerId = form.getConsumerId();

        Long cmmSerializedProductId = form.getCmmSerializedProductId();
        String relationTypeId = form.getRelationType();

        CmmConsumerSerialProRelationVO result = svm0104Service.findConsumerAndMcRelation(cmmSerializedProductId, consumerId);

        if (result == null) {
            result = new CmmConsumerSerialProRelationVO();

            result.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
            result.setSerializedProductId(cmmSerializedProductId);
            result.setFromDate(ComUtil.nowLocalDate());
            result.setToDate(CommonConstants.MAX_DATE);
        }
        result.setConsumerId(consumerId); // 变更consumer
        result.setConsumerSerializedProductRelationTypeId(relationTypeId);
        //若relationType = 'S022OWNER', 把ownerFlg设置为Y, 反之为N
        result.setOwnerFlag(StringUtils.equals(relationTypeId, ConsumerSerialProRelationType.OWNER.getCodeDbid()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);

        return result;
    }

    /**
     * 保存前check
     */
    private void validateBeforeSave(SVM010402Form form) {

        // 车牌号若为空则报错
        if (StringUtils.isBlank(form.getPlateNo())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10317", new String[] { ComUtil.t("label.numberPlate") }));
        }

        // 车牌号若在cmm_serialized_product中存在则报错
        if (StringUtils.isNotBlank(form.getPlateNo())) {
            // 通过车牌号去cmm_serialized_product检索
            CmmSerializedProductVO cmmSerProVO = svm0104Service.getCmmSerialProductByPlate(form.getPlateNo());
            if (!Nulls.isNull(cmmSerProVO) && !cmmSerProVO.getSerializedProductId().equals(form.getSerializedProductId())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00310", new String[] { ComUtil.t("label.serialNumberType"), SERIALPLATENO_STR, ComUtil.t("label.serialNumber"), form.getPlateNo(), ComUtil.t("label.tableProductSerialNumber") }));
            }
        }

        // 车型校验
        if (StringUtils.isNotBlank(form.getModelCode())) {

            // 获取mstProductVOList
            Set<String> productCdSet = new HashSet<>();
            productCdSet.add(form.getModelCode());
            List<MstProductVO> mstProductVOList = svm0104Service.getMstProductList(productCdSet, ProductClsType.GOODS.getCodeDbid());
            // 如果modelCd在mst_product中不存在则报错
            if (mstProductVOList.isEmpty()) {
                throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] { ComUtil.t("label.model"), form.getModelCode(), ComUtil.t("label.productInformation") }));
            }
        }

        // 若销售状态 != S040SALESTOUSER则报错
        if (!StringUtils.equals(McSalesStatus.SALESTOUSER, form.getSalesStatus())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10320", new String[] { ComUtil.t("label.frameNumber") }));
        }

        // 销售日期相关校验
        if (StringUtils.isNotBlank(form.getSoldDate())) {
            LocalDate soldDate = ComUtil.str2date(form.getSoldDate());
            // 若销售日期大于当前系统日期则报错
            if (soldDate.isAfter(LocalDate.now())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00202", new String[] { ComUtil.t("label.soldDate"), ComUtil.t("label.stuDate") }));
            }
            // 若销售日期小于出厂日期则报错
            if (StringUtils.isNotBlank(form.getManufacturingDate())) {
                LocalDate manufacturingDate = ComUtil.str2date(form.getManufacturingDate());
                if (soldDate.isBefore(manufacturingDate)) {
                    throw new BusinessCodedException(ComUtil.t("M.E.00202", new String[] { ComUtil.t("label.assemblyDate"), ComUtil.t("label.soldDate") }));
                }
            }
        }
    }

    /**
     * 电池Check
     */
    private void validationBattery(String batteryId1, String batteryId2
                                , Map<String, BatteryVO> batteryMap, Map<String, CmmBatteryVO> CmmBatteryMap) {

        // 若电池1和电池2为空则报错
        if (StringUtils.isBlank(batteryId1)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10317", new String[] { ComUtil.t("label.batteryId1") }));
        }
        if (StringUtils.isBlank(batteryId2)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10317", new String[] { ComUtil.t("label.batteryId2") }));
        }

        // 若电池长度必须不等于17位则报错
        if (batteryId1.length() != 17) {
            throw new BusinessCodedException(ComUtil.t("M.E.10322", new String[] { ComUtil.t("label.batteryId1"), "17" }));
        }
        if (batteryId2.length() != 17) {
            throw new BusinessCodedException(ComUtil.t("M.E.10322", new String[] { ComUtil.t("label.batteryId2"), "17" }));
        }

        // 若batteryId1 = batteryId2则报错
        if (StringUtils.equals(batteryId1, batteryId2)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10316", new String[] { ComUtil.t("label.batteryId1"), ComUtil.t("label.batteryId2") }));
        }

        // 若电池1和电池2在battery中不存在或battery_status != S084SHIPPED则报错
        BatteryVO battery1 = batteryMap.get(batteryId1);
        if (Nulls.isNull(battery1) || !StringUtils.equals(battery1.getBatteryStatus(), SdStockStatus.SHIPPED.getCodeDbid())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10318", new String[] { batteryId1 }));
        }

        BatteryVO battery2 = batteryMap.get(batteryId2);;
        if (Nulls.isNull(battery2) || !StringUtils.equals(battery2.getBatteryStatus(), SdStockStatus.SHIPPED.getCodeDbid())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10318", new String[] { batteryId2 }));
        }

        // 若电池1和电池2在cmm_battery中不存在则报错
        CmmBatteryVO cmmBattery1 = CmmBatteryMap.get(batteryId1);
        if (Nulls.isNull(cmmBattery1)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10319", new String[] { ComUtil.t("label.batteryId1") }));
        }

        CmmBatteryVO cmmBattery2 = CmmBatteryMap.get(batteryId2);
        if (Nulls.isNull(cmmBattery2)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10319", new String[] { ComUtil.t("label.batteryId2") }));
        }

        // 若电池1和电池2在cmm_battery中的状态battery_status != S084SHIPPED则报错
        if (!StringUtils.equals(cmmBattery1.getBatteryStatus(), SdStockStatus.SHIPPED.getCodeDbid())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10320", new String[] { ComUtil.t("label.batteryId1") }));
        }
        if (!StringUtils.equals(cmmBattery2.getBatteryStatus(), SdStockStatus.SHIPPED.getCodeDbid())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10320", new String[] { ComUtil.t("label.batteryId2") }));
        }

        // 若电池1和电池2在cmm_battery中的serialized_product_id不为空则报错
        if (!Nulls.isNull(cmmBattery1.getSerializedProductId())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10321", new String[] { ComUtil.t("label.batteryId1") }));
        }

        if (!Nulls.isNull(cmmBattery2.getSerializedProductId())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10321", new String[] { ComUtil.t("label.batteryId2") }));
        }
    }

    /**
     * 更新旧电池
     */
    private void updateOldBattery(List<CmmBatteryVO> updCmmBatteryList
                                , List<BatteryVO> updBatteryList
                                , Map<String, CmmBatteryVO> CmmBatteryMap
                                , Map<String, BatteryVO> batteryMap
                                , String oldBatteryNo
                                , String sysDate) {

        // 获取oldBatteryVO
        if (batteryMap.containsKey(oldBatteryNo)) {
            BatteryVO oldBatteryVO = batteryMap.get(oldBatteryNo);
            oldBatteryVO.setToDate(sysDate);

            updBatteryList.add(oldBatteryVO);
        }
        if (CmmBatteryMap.containsKey(oldBatteryNo)) {
            CmmBatteryVO oldCmmBatteryVO = CmmBatteryMap.get(oldBatteryNo);
            oldCmmBatteryVO.setToDate(sysDate);

            updCmmBatteryList.add(oldCmmBatteryVO);
        }
    }

    /**
     * 更新新电池
     */
    private void updateNewBattery(List<CmmBatteryVO> updCmmBatteryList
                                , List<BatteryVO> updBatteryList
                                , Map<String, BatteryVO> batteryMap
                                , Map<String, CmmBatteryVO> CmmBatteryMap
                                , SVM010402Form form
                                , String batteryType
                                , String sysDate
                                , String maxDate) {

        String batteryNo = StringUtils.equals(batteryType, BatteryType.TYPE1.getCodeDbid())? form.getBatteryId1() : form.getBatteryId2();
        //获取newBatteryVO
        BatteryVO newBattery = batteryMap.get(batteryNo);

        if (batteryMap.containsKey(batteryNo)) {

            newBattery.setFromDate(sysDate);
            newBattery.setToDate(maxDate);
            newBattery.setPositionSign(batteryType);
            newBattery.setSerializedProductId(form.getSerializedProductId());

            updBatteryList.add(newBattery);

            //获取newCmmBatteryVO
            CmmBatteryVO newCmmBattery = CmmBatteryMap.get(batteryNo);
            if (CmmBatteryMap.containsKey(batteryNo)) {

                newCmmBattery.setFromDate(sysDate);
                newCmmBattery.setToDate(maxDate);
                newCmmBattery.setPositionSign(batteryType);
                newCmmBattery.setSerializedProductId(form.getCmmSerializedProductId());

                updCmmBatteryList.add(newCmmBattery);
            }
        }
    }
}
