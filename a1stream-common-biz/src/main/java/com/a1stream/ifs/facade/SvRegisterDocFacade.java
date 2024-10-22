package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.constants.PJConstants.CategoryCd;
import com.a1stream.common.constants.PJConstants.GenderType;
import com.a1stream.common.constants.PJConstants.SerialProQualityStatus;
import com.a1stream.common.constants.PJConstants.SerialproductStockStatus;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.ConsumerParam;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.ifs.bo.SvRegisterDocAbstractBO;
import com.a1stream.ifs.bo.SvRegisterDocBO;
import com.a1stream.ifs.bo.SvRegisterDocChangeBO;
import com.a1stream.ifs.bo.SvRegisterDocParam;
import com.a1stream.ifs.service.IfsCommService;
import com.a1stream.ifs.service.SvRegisterDocService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SvRegisterDocFacade {

    @Resource
    private SvRegisterDocService registDocSer;

    @Resource
    private IfsCommService cmmSiteMstSer;

    private static final String KEY_CITY_UNKNOWN = "Unknown";

    /**
     * IX_svRegisterDoc
     * importRegisterDocumentsregistrationDocumentManager.doReceiveNewRegisterationDocumentFromIf
     */
    public void importRegisterDocuments(List<SvRegisterDocBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        // 启用的经销商列表
        Set<String> dealerCds = dataList.stream().map(SvRegisterDocBO::getDealerCode).collect(Collectors.toSet());
        Map<String, CmmSiteMasterVO> activeDealerMap = cmmSiteMstSer.getActiveDealerMap(dealerCds);

        // Extracting dealer code as the key
        Function<SvRegisterDocBO, String> keyExtractor = SvRegisterDocBO::getDealerCode;
        Map<String, List<SvRegisterDocBO>> siteDataMap = cmmSiteMstSer.groupBySite(dataList, keyExtractor, activeDealerMap);

        SvRegisterDocParam param = prepareSvRegisterDocParams(dataList, activeDealerMap);

        for(String siteId : activeDealerMap.keySet()) {
            List<SvRegisterDocBO> items = siteDataMap.get(siteId);
            for (SvRegisterDocBO item : items) {
                if (StringUtils.isEmpty(item.getFrameNo())) {
                    doAddOrUpdateBatteryInfo(item, item.getBatteryId1(), null, null, param);
                    buildConsumerInfo(item, siteId, param);
                    doUpdRegistDocForBatteryDtl(item, siteId, param);
                } else {
                    Map<String, CmmSerializedProductVO> serialProductMap = param.getSerialProductMap();
                    Map<Long, SerializedProductVO> productMap = param.getProductMap();
                    if (serialProductMap.containsKey(item.getFrameNo())) {
                        CmmSerializedProductVO cmmSerialProduct = serialProductMap.get(item.getFrameNo());
                        Long serialProductId = cmmSerialProduct.getSerializedProductId();
                        Long productId = cmmSerialProduct.getProductId();

                        String status = StringUtils.isEmpty(cmmSerialProduct.getSalesStatus())? McSalesStatus.SALESTOUSER : cmmSerialProduct.getSalesStatus();

                        if (StringUtils.equals(status, SerialproductStockStatus.ONTRANSFER)) { // TODO
                            // message

                            // save data to temp table
                        } else {
                            Map<Long, CmmRegistrationDocumentVO> serialProductRegistDocMap = param.getSerialProductRegistDocMap();
                            Long consumerId;
                            if (serialProductRegistDocMap.containsKey(serialProductId)) {
                                CmmRegistrationDocumentVO serialProductRegistDoc = serialProductRegistDocMap.get(serialProductId);
                                consumerId = serialProductRegistDoc.getConsumerId();
                            } else {
                                buildConsumerInfo(item, siteId, param);
                                consumerId = param.getConsumerId();
                                // TODO doUpdateCmmConsumerSerializedProductRelation
                                //更新人车关系
//                                prepareConsumerMotorRelation(serviceOrder, para);
                                //更新regsiterDoc
//                                prepareRegisterDoc(serviceOrder, para);
                            }
                            if (StringUtils.equals(status, McSalesStatus.STOCK)) {
                                // do local SerializedProduct sale out
                                if (productMap.containsKey(serialProductId)) {
                                    SerializedProductVO serialProduct = productMap.get(serialProductId);
                                    String stockSts = serialProduct.getStockStatus();
                                    String salesSts = serialProduct.getSalesStatus();
                                    String qualitySts = serialProduct.getQualityStatus();
                                    if (!StringUtils.equals(qualitySts, SerialProQualityStatus.NORMAL)) {
                                        log.warn("Input frameNo :" + item.getFrameNo() + "has an unnormal ststus :" + qualitySts);
                                        continue;
                                    } else {
                                        if (StringUtils.equals(stockSts, SerialproductStockStatus.ONHAND)) {
                                            // TODO doVehicleSalesOut
                                        } else if (StringUtils.equals(salesSts, SerialproductStockStatus.ONTRANSFER)) {
                                            // TODO doVehicleReceive
                                            // TODO doVehicleSalesOut
                                        }
                                    }
                                }
                            }
                            if (StringUtils.equals(item.getCategoryType(), CategoryCd.EV)) {
                                doAddOrUpdateBatteryInfo(item, item.getBatteryId1(), serialProductId, productId, param);
                                doAddOrUpdateBatteryInfo(item, item.getBatteryId2(), serialProductId, productId, param);
                            }
                            // TODO insertQueueApiData
                        }
                    } else {
                        log.warn("can not get serializedProduct from input frameNo :"+ item.getFrameNo());
                    }
                }
            }
        }

        registDocSer.maintainData(param);
    }

    /**
     * IX_svRegisterDocChange
     * importRegisterDocumentChanges registrationDocumentManager.doReceiveChangedRegisterationDocumentFromIf
     */
    public void importRegisterDocumentChanges(List<SvRegisterDocChangeBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        // 启用的经销商列表
        Set<String> dealerCds = dataList.stream().map(SvRegisterDocChangeBO::getDealerCode).collect(Collectors.toSet());
        Map<String, CmmSiteMasterVO> activeDealerMap = cmmSiteMstSer.getActiveDealerMap(dealerCds);

        // Extracting dealer code as the key
        Function<SvRegisterDocChangeBO, String> keyExtractor = SvRegisterDocChangeBO::getDealerCode;
        Map<String, List<SvRegisterDocChangeBO>> siteDataMap = cmmSiteMstSer.groupBySite(dataList, keyExtractor, activeDealerMap);

        SvRegisterDocParam param = prepareSvRegisterDocChangeParams(dataList, activeDealerMap);

        for(String siteId : activeDealerMap.keySet()) {
            List<SvRegisterDocChangeBO> items = siteDataMap.get(siteId);

            for (SvRegisterDocChangeBO itemModel : items) {
                if (StringUtils.isEmpty(itemModel.getFrameNo())) {
                    doAddOrUpdateBatteryInfo(itemModel, itemModel.getBatteryId1(), null, null, param);
                    buildConsumerInfo(itemModel, siteId, param);
                    doUpdRegistDocForBatteryDtl(itemModel, siteId, param);
                } else {
                    Map<String, CmmSerializedProductVO> serialProductMap = param.getSerialProductMap();
                    Map<Long, SerializedProductVO> productMap = param.getProductMap();
                    if (serialProductMap.containsKey(itemModel.getFrameNo())) {
                        CmmSerializedProductVO cmmSerialProduct = serialProductMap.get(itemModel.getFrameNo());
                        Long serialProductId = cmmSerialProduct.getSerializedProductId();
                        Long productId = cmmSerialProduct.getProductId();

                        String dataType = itemModel.getDataType();

                        if (StringUtils.equals(dataType, "TRANSFER")) {
                            cmmSerialProduct.setStuDate(itemModel.getSalesDate());
                            // TODO 更新人车关系

                        }
                        if (StringUtils.equals(dataType, "MODIFY")) {
                            String oldStuDate = cmmSerialProduct.getStuDate();
                            if (StringUtils.equals(oldStuDate, itemModel.getSalesDate())) {
                                // get CmmRemindSchedule

                                // doRemoveServiceRemind
                            }

                            cmmSerialProduct.setStuDate(itemModel.getSalesDate());
                            // TODO 更新人车关系

                        }

                        if (StringUtils.equals(dataType, "CANCEL")) {
                            // TODO remove cmmRegistInfo

                            // doReturnSerializedProductSaleOut

                        }

                        if (StringUtils.equals(itemModel.getCategoryType(), CategoryCd.EV)) {
                            doAddOrUpdateBatteryInfo(itemModel, itemModel.getBatteryId1(), serialProductId, productId, param);
                            doAddOrUpdateBatteryInfo(itemModel, itemModel.getBatteryId2(), serialProductId, productId, param);
                            // update consumer

                            doUpdRegistDocForBatteryDtl(itemModel, siteId, param);
                        }

                        // TODO insertQueueApiData
                    } else {
                        log.warn("can not get serializedProduct from input frameNo :"+ itemModel.getFrameNo());
                    }
                }
            }
        }
    }

    private void doAddOrUpdateBatteryInfo(SvRegisterDocAbstractBO item, String batteryId, Long serialProductId, Long productId, SvRegisterDocParam param) {

        Map<String, CmmBatteryVO> cmmBatteryNoMap = param.getCmmBatteryVOMap();
        Map<String, BatteryVO> batteryNoMap = param.getBatteryVOMap();

        String originalType = StringUtils.equals(item.getCategoryType(), CategoryCd.EV)? CommonConstants.CHAR_Y : CommonConstants.CHAR_N;
        if (!StringUtils.isEmpty(batteryId)) {
            CmmBatteryVO cmmBatteryVO;
            BatteryVO batteryVO;
            if (batteryNoMap.containsKey(batteryId)) {
                cmmBatteryVO = cmmBatteryNoMap.get(batteryId);
                cmmBatteryVO.setBatteryStatus(SerialproductStockStatus.SHIPPED);

                batteryVO = batteryNoMap.get(batteryId);
                batteryVO.setBatteryStatus(SerialproductStockStatus.SHIPPED);
            } else {
                cmmBatteryVO = new CmmBatteryVO();

                cmmBatteryVO.setBatteryNo(batteryId);
                cmmBatteryVO.setBatteryStatus(SerialproductStockStatus.SHIPPED);
                cmmBatteryVO.setSellingPrice(BigDecimal.ZERO);
                cmmBatteryVO.setSaleDate(ComUtil.nowDate());
                cmmBatteryVO.setSiteId(item.getDealerCode());
                cmmBatteryVO.setServiceCalculateDate(item.getSalesDate());
                cmmBatteryVO.setOriginalFlag(originalType);

                batteryVO = BatteryVO.copyFromCmm(cmmBatteryVO, item.getDealerCode(), null);
                batteryVO.setSerializedProductId(serialProductId);
                batteryVO.setProductId(productId);
            }

            param.getUpdCmmBatteryList().add(cmmBatteryVO);
            param.getUpdBatteryList().add(batteryVO);
        }
    }

    private void buildConsumerInfo(SvRegisterDocAbstractBO item, String siteId, SvRegisterDocParam param) {

        BaseConsumerForm consumerForm = prepareConsumerBasicInfo(item, siteId, param.getCityGeographyMap());
        ConsumerParam consumerParam = registDocSer.prepareConsumerData(consumerForm);

        param.getUpdCmmConsumerList().add(consumerParam.getCmmConsumerVO());
        param.getUpdConsumerPrivateList().add(consumerParam.getConsumerPrivateDetailVO());
        param.setConsumerId(consumerParam.getConsumerId());
    }

    private void doUpdRegistDocForBatteryDtl(SvRegisterDocAbstractBO item, String siteId, SvRegisterDocParam param) {

        Map<Long, CmmRegistrationDocumentVO> registDocForBatteryMap = param.getRegistDocBatteryMap();
        Map<String, MstFacilityVO> facilityMap = param.getFacilityMap();
        Map<String, BatteryVO> batteryVOMap = param.getBatteryVOMap();
        Long batteryId1 = batteryVOMap.get(item.getBatteryId1()).getBatteryId();
        Long batteryId2 = batteryVOMap.get(item.getBatteryId2()).getBatteryId();

        if (registDocForBatteryMap.containsKey(batteryId1)) {
            CmmRegistrationDocumentVO registDoc = registDocForBatteryMap.get(batteryId1);

            buildCmmRegistDoc(item, siteId, param, facilityMap, registDoc);
        }
        if (registDocForBatteryMap.containsKey(batteryId2)) {
            CmmRegistrationDocumentVO registDoc = registDocForBatteryMap.get(batteryId2);

            buildCmmRegistDoc(item, siteId, param, facilityMap, registDoc);
        }
    }

    /**
     * @param item
     * @param siteId
     * @param param
     * @param facilityMap
     * @param registDoc
     */
    private void buildCmmRegistDoc(SvRegisterDocAbstractBO item, String siteId, SvRegisterDocParam param, Map<String, MstFacilityVO> facilityMap, CmmRegistrationDocumentVO registDoc) {

        registDoc.setSiteId(siteId);
        registDoc.setRegistrationDate(item.getSalesDate());
        registDoc.setConsumerId(param.getConsumerId());
//            registDoc.setRegistrationDocumentNo(item.getRegistrationNo());
        registDoc.setFacilityId(facilityMap.get(item.getRegistrationPointCode()).getFacilityId());

        param.getUpdRegistDocList().add(registDoc);
    }

    private BaseConsumerForm prepareConsumerBasicInfo(SvRegisterDocAbstractBO item, String siteId, Map<String, CmmGeorgaphyVO> cityGeographyMap){

        BaseConsumerForm model = new BaseConsumerForm();

        String ifGenderCode = StringUtils.isEmpty(item.getSex())? "L" : item.getSex();
        String firstNm = StringUtils.isEmpty(item.getBusinessNameFirst())? item.getOwnerNameFirst() : item.getBusinessNameFirst();
        String lastNm = StringUtils.isEmpty(item.getBusinessNameLast())? item.getOwnerNameLast() : item.getBusinessNameLast();
        String city = StringUtils.isEmpty(item.getCity())? KEY_CITY_UNKNOWN : item.getCity();

        model.setSiteId(siteId);
        model.setFirstNm(firstNm);
        model.setLastNm(lastNm);
        model.setGender(GenderType.ifTransfer.get(ifGenderCode));
        model.setRegistDate(item.getSalesDate());
        model.setComment(item.getComment());
        model.setBusinessNm(item.getBusinessName());
        if (cityGeographyMap.containsKey(city)) {
            CmmGeorgaphyVO cityGeo = cityGeographyMap.get(city);
            model.setDistrict(cityGeo.getGeographyId());
            model.setProvince(cityGeo.getParentGeographyId());
        }
        model.setAddress(item.getAddress1());
        model.setAddress2(item.getAddress2());
        model.setBirthDate(item.getBirthday());
        model.setMobilePhone(item.getCellphoneNumber());
        model.setTelephone(item.getTelephoneNumber());
        model.setEmail(item.getEmailPrimary());
        model.setEmail2(item.getEmailSecondary());
        model.setOccupation(item.getOccupationTp());

        return model;
    }

    private SvRegisterDocParam prepareSvRegisterDocParams(List<SvRegisterDocBO> dataList, Map<String, CmmSiteMasterVO> activeDealerMap) {

        SvRegisterDocParam param = new SvRegisterDocParam();

        Set<String> siteIdSet = activeDealerMap.keySet();

        Set<String> pointCds = new HashSet<>();
        Set<String> batteryNos = new HashSet<>();
        Set<String> cityCds = new HashSet<>();
        Set<String> frameNos = new HashSet<>();

        for(SvRegisterDocAbstractBO data : dataList) {
            collectionByDataList(pointCds, batteryNos, cityCds, frameNos, data);
        }

        setValueToParamBO(param, siteIdSet, pointCds, batteryNos, cityCds, frameNos);

        return param;
    }

    private SvRegisterDocParam prepareSvRegisterDocChangeParams(List<SvRegisterDocChangeBO> dataList, Map<String, CmmSiteMasterVO> activeDealerMap) {

        SvRegisterDocParam param = new SvRegisterDocParam();

        Set<String> siteIdSet = activeDealerMap.keySet();

        Set<String> pointCds = new HashSet<>();
        Set<String> batteryNos = new HashSet<>();
        Set<String> cityCds = new HashSet<>();
        Set<String> frameNos = new HashSet<>();

        for(SvRegisterDocAbstractBO data : dataList) {
            collectionByDataList(pointCds, batteryNos, cityCds, frameNos, data);
        }

        setValueToParamBO(param, siteIdSet, pointCds, batteryNos, cityCds, frameNos);

        return param;
    }

    /**
     * @param param
     * @param siteIdSet
     * @param pointCds
     * @param batteryNos
     * @param cityCds
     * @param frameNos
     */
    private void setValueToParamBO(SvRegisterDocParam param, Set<String> siteIdSet, Set<String> pointCds, Set<String> batteryNos, Set<String> cityCds, Set<String> frameNos) {

        Map<String, CmmBatteryVO> cmmBatteryVOMap = registDocSer.findCmmBatteryNoMap(batteryNos);
        Set<Long> batteryIds = cmmBatteryVOMap.values().stream().map(CmmBatteryVO::getBatteryId).collect(Collectors.toSet());
        Map<String, CmmSerializedProductVO> serialProductMap = registDocSer.getSerialProductMap(frameNos);
        Set<Long> serialProductIds = serialProductMap.values().stream().map(CmmSerializedProductVO::getSerializedProductId).collect(Collectors.toSet());

        Map<Long, SerializedProductVO> productMap = registDocSer.getProductMap(serialProductIds);

        param.setCmmBatteryVOMap(cmmBatteryVOMap);
        param.setBatteryVOMap(registDocSer.findBatteryNoMap(batteryNos));
        param.setRegistDocBatteryMap(registDocSer.findRegistDocMap(batteryIds));
        param.setCityGeographyMap(registDocSer.getCityGeoMap(cityCds));
        param.setFacilityMap(cmmSiteMstSer.getFacilityMap(siteIdSet, pointCds));
        param.setSerialProductMap(serialProductMap);
        param.setProductMap(productMap);
        param.setSerialProductRegistDocMap(registDocSer.getSerialProdRegistDocMap(serialProductIds));
    }

    /**
     * @param pointCds
     * @param batteryNos
     * @param cityCds
     * @param frameNos
     * @param data
     */
    private void collectionByDataList(Set<String> pointCds, Set<String> batteryNos, Set<String> cityCds, Set<String> frameNos, SvRegisterDocAbstractBO data) {

        pointCds.add(data.getRegistrationPointCode());
        batteryNos.add(data.getBatteryId1());
        cityCds.add(data.getCity());
        frameNos.add(data.getFrameNo());
    }
}
