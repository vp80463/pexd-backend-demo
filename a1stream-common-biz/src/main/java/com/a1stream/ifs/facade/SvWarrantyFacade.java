package com.a1stream.ifs.facade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandDetailVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.CmmWarrantySerializedProductVO;
import com.a1stream.ifs.bo.SvWarrantyBO;
import com.a1stream.ifs.bo.SvWarrantyItemBO;
import com.a1stream.ifs.bo.SvWarrantyParam;
import com.a1stream.ifs.service.SvWarrantyService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SvWarrantyFacade {

    @Resource
    private SvWarrantyService warrantySer;

    private static final String Y = CommonConstants.CHAR_Y;
    private static final String N = CommonConstants.CHAR_N;
    private static final String SITE_666N = CommonConstants.CHAR_DEFAULT_SITE_ID;

    /**
     * IX_svWarranty
     * INewWarrantySerializedProductImportLogic doNewWarrantySerializedProductImport
     */
    public void importWarranty(List<SvWarrantyBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        SvWarrantyParam param = new SvWarrantyParam();

        List<SvWarrantyBO> batteryList = dataList.stream().filter(item -> StringUtils.equals(item.getCategoryType(), BatteryType.CODE_ID)).collect(Collectors.toList());
        List<SvWarrantyBO> mcList = dataList.stream().filter(item -> !StringUtils.equals(item.getCategoryType(), BatteryType.CODE_ID)).collect(Collectors.toList());

        if (!mcList.isEmpty()) {
            prepareMcParams(mcList, param);
            newOrUpdMCWarranty(mcList, param);
        }
        if (!batteryList.isEmpty()) {
            newOrUpdBatteryWarranty(batteryList, param);
        }

        warrantySer.maintainData(param);
    }

    private void newOrUpdBatteryWarranty(List<SvWarrantyBO> batteryList, SvWarrantyParam param) {

        Map<String,SvWarrantyBO> insModelData = new HashMap<>();
        Map<String,SvWarrantyBO> updModelData = new HashMap<>();
        Map<String, SvWarrantyBO> frameNoWithModelMap = batteryList.stream().collect(Collectors.toMap(SvWarrantyBO::getFrameNo, Function.identity()));

        Map<String, CmmBatteryVO> existBatteryMap = warrantySer.getCmmBatteryMap(frameNoWithModelMap.keySet());
        Map<Long, CmmBatteryVO> existBatteryIdMap = existBatteryMap.values().stream().collect(Collectors.toMap(CmmBatteryVO::getBatteryId, Function.identity()));
        Set<Long> batteryIds = existBatteryMap.values().stream().map(CmmBatteryVO::getBatteryId).collect(Collectors.toSet());

        Map<Long, CmmWarrantyBatteryVO> warrantyBatteryMap = warrantySer.getWarrantyBatteryMap(batteryIds);

        getInsOrUpdBatteryData(insModelData, updModelData, frameNoWithModelMap, existBatteryMap);

        prepareNewOrUpdWarrantyBatteryVO(param, insModelData, updModelData, existBatteryMap, existBatteryIdMap, warrantyBatteryMap);
    }

    private void newOrUpdMCWarranty(List<SvWarrantyBO> mcList, SvWarrantyParam param) {

        Map<Long, SvWarrantyBO> prodIdAndBOMap = param.getProdIdAndBOMap();
        Map<Long, List<SvWarrantyItemBO>> prodIdAndItemBOMap = param.getProdIdAndItemBOMap();

        Map<Long, String> svDemandMap = param.getSvDemandMap();
        Map<Long, List<CmmServiceDemandDetailVO>> svDemandDtlMap = param.getSvDemandDtlMap();
        Map<Long, CmmSerializedProductVO> prodIdAndSerProdMap = param.getProdIdAndSerProdMap();
        Map<Long, CmmWarrantySerializedProductVO> warrantyPolicyMap = param.getWarrantyPolicyMap();

        Map<Long, SvWarrantyBO> insBOMap = new HashMap<>();
        Map<Long, SvWarrantyBO> updBOMap = new HashMap<>();
        Map<Long, List<SvWarrantyItemBO>> insItemBOMap = new HashMap<>();
        Map<Long, List<SvWarrantyItemBO>> updItemBOMap = new HashMap<>();

        getInsOrUpdMcData(prodIdAndBOMap, prodIdAndItemBOMap, warrantyPolicyMap, insBOMap, updBOMap, insItemBOMap, updItemBOMap);

        prepareNewSvDemandDtlVO(param, svDemandMap, prodIdAndSerProdMap, insItemBOMap);

        prepareUpdSvDemandDtlVO(param, svDemandMap, svDemandDtlMap, prodIdAndSerProdMap, updItemBOMap);

        prepareNewOrUpdWarrantySerialProdVO(param, prodIdAndSerProdMap, warrantyPolicyMap, insBOMap, updBOMap);
    }

    /**
     * @param param
     * @param insModelData
     * @param updModelData
     * @param existBatteryMap
     * @param existBatteryIdMap
     * @param warrantyBatteryMap
     */
    private void prepareNewOrUpdWarrantyBatteryVO(SvWarrantyParam param, Map<String, SvWarrantyBO> insModelData, Map<String, SvWarrantyBO> updModelData, Map<String, CmmBatteryVO> existBatteryMap, Map<Long, CmmBatteryVO> existBatteryIdMap, Map<Long, CmmWarrantyBatteryVO> warrantyBatteryMap) {
        for (String batteryId : insModelData.keySet()) {

            SvWarrantyBO model = insModelData.get(batteryId);
            CmmBatteryVO cmmBattery = existBatteryMap.get(batteryId);

            buildCmmWarrantyBattery(null, cmmBattery, model, param);
        }

        for (Long batteryId : warrantyBatteryMap.keySet()) {

            String batteryNo = existBatteryIdMap.get(batteryId).getBatteryNo();

            SvWarrantyBO model = updModelData.get(batteryNo);

            buildCmmWarrantyBattery(warrantyBatteryMap.get(batteryId), null, model, param);
        }
    }

    /**
     * @param insModelData
     * @param updModelData
     * @param frameNoWithModelMap
     * @param existBatteryMap
     */
    private void getInsOrUpdBatteryData(Map<String, SvWarrantyBO> insModelData, Map<String, SvWarrantyBO> updModelData, Map<String, SvWarrantyBO> frameNoWithModelMap, Map<String, CmmBatteryVO> existBatteryMap) {
        for (String key : frameNoWithModelMap.keySet()) {
            if (existBatteryMap.containsKey(key)) {
                updModelData.put(key, frameNoWithModelMap.get(key));
            } else {
                insModelData.put(key, frameNoWithModelMap.get(key));
            }
        }
    }

    /**
     * @param param
     * @param prodIdAndSerProdMap
     * @param warrantyPolicyMap
     * @param insBOMap
     * @param updBOMap
     */
    private void prepareNewOrUpdWarrantySerialProdVO(SvWarrantyParam param, Map<Long, CmmSerializedProductVO> prodIdAndSerProdMap, Map<Long, CmmWarrantySerializedProductVO> warrantyPolicyMap, Map<Long, SvWarrantyBO> insBOMap, Map<Long, SvWarrantyBO> updBOMap) {
        for(Long serialProdId : insBOMap.keySet()) {

            buildCmmWarrantySerialProd(null, prodIdAndSerProdMap.get(serialProdId), insBOMap.get(serialProdId), param);
        }

        for (CmmWarrantySerializedProductVO member : warrantyPolicyMap.values()) {

            SvWarrantyBO model = updBOMap.get(member.getSerializedProductId());
            buildCmmWarrantySerialProd(member, prodIdAndSerProdMap.get(member.getSerializedProductId()), model, param);
        }
    }

    /**
     * @param param
     * @param svDemandMap
     * @param svDemandDtlMap
     * @param prodIdAndSerProdMap
     * @param updItemBOMap
     */
    private void prepareUpdSvDemandDtlVO(SvWarrantyParam param, Map<Long, String> svDemandMap, Map<Long, List<CmmServiceDemandDetailVO>> svDemandDtlMap, Map<Long, CmmSerializedProductVO> prodIdAndSerProdMap, Map<Long, List<SvWarrantyItemBO>> updItemBOMap) {
        Map<Long, SvWarrantyItemBO> importedSvDemandMap = new HashMap<>();
        for (Long serialProdId : updItemBOMap.keySet()) {
            CmmSerializedProductVO cmmSerialProd = prodIdAndSerProdMap.get(serialProdId);
            for (SvWarrantyItemBO itemModel : updItemBOMap.get(serialProdId)) {
                Long svDemandId = Long.valueOf(itemModel.getCouponCategoryLevel());
                if (svDemandMap.containsKey(svDemandId)) {
                    importedSvDemandMap.put(svDemandId, itemModel);
                }
            }
            Map<Long, CmmServiceDemandDetailVO> existSvDemandDtlMap = svDemandDtlMap.get(serialProdId).stream().collect(Collectors.toMap(CmmServiceDemandDetailVO::getServiceDemandId, Function.identity()));

            for (Long importedSvDemandId : importedSvDemandMap.keySet()) {
                if (existSvDemandDtlMap.containsKey(importedSvDemandId)) {
                    buildSvDemandDtl(existSvDemandDtlMap.get(importedSvDemandId), cmmSerialProd, importedSvDemandId, importedSvDemandMap.get(importedSvDemandId), param);
                } else {
                    buildSvDemandDtl(null, cmmSerialProd, importedSvDemandId, importedSvDemandMap.get(importedSvDemandId), param);
                }
            }
        }
    }

    /**
     * @param param
     * @param svDemandMap
     * @param prodIdAndSerProdMap
     * @param insItemBOMap
     */
    private void prepareNewSvDemandDtlVO(SvWarrantyParam param, Map<Long, String> svDemandMap, Map<Long, CmmSerializedProductVO> prodIdAndSerProdMap, Map<Long, List<SvWarrantyItemBO>> insItemBOMap) {
        for (Long serialProdId : insItemBOMap.keySet()) {

            Map<String, SvWarrantyItemBO> itemModelMap = insItemBOMap.get(serialProdId).stream().collect(Collectors.toMap(SvWarrantyItemBO::getCouponCategoryLevel, Function.identity()));
            for (Long svDemandId : svDemandMap.keySet()) {
                String seqNo = StringUtils.toString(svDemandId);
                CmmSerializedProductVO cmmSerialProd = prodIdAndSerProdMap.get(serialProdId);
                if (!itemModelMap.containsKey(seqNo)) {
                    // For EV FSC information , the EV just have 6 times
                    if (svDemandId > 6 && !StringUtils.equals(cmmSerialProd.getEvFlag(), Y)) {
                        buildSvDemandDtl(null, cmmSerialProd, svDemandId, null, param);
                    }
                } else {
                    buildSvDemandDtl(null, cmmSerialProd, svDemandId, itemModelMap.get(seqNo), param);
                }
            }
        }
    }

    /**
     * @param prodIdAndBOMap
     * @param prodIdAndItemBOMap
     * @param warrantyPolicyMap
     * @param insBOMap
     * @param updBOMap
     * @param insItemBOMap
     * @param updItemBOMap
     */
    private void getInsOrUpdMcData(Map<Long, SvWarrantyBO> prodIdAndBOMap, Map<Long, List<SvWarrantyItemBO>> prodIdAndItemBOMap, Map<Long, CmmWarrantySerializedProductVO> warrantyPolicyMap, Map<Long, SvWarrantyBO> insBOMap, Map<Long, SvWarrantyBO> updBOMap, Map<Long, List<SvWarrantyItemBO>> insItemBOMap, Map<Long, List<SvWarrantyItemBO>> updItemBOMap) {
        for(Long serialProdId : prodIdAndBOMap.keySet()) {
            SvWarrantyBO bo = prodIdAndBOMap.get(serialProdId);

            List<SvWarrantyItemBO> boItems = prodIdAndItemBOMap.get(serialProdId);
            if (!warrantyPolicyMap.containsKey(serialProdId)) {
                insBOMap.put(serialProdId, bo);
            } else {
                updBOMap.put(serialProdId, bo);
            }
            if (!prodIdAndItemBOMap.containsKey(serialProdId)) {
                insItemBOMap.get(serialProdId).addAll(boItems);
            } else {
                updItemBOMap.put(serialProdId, boItems);
            }
        }
    }

    private void buildCmmWarrantyBattery(CmmWarrantyBatteryVO cmmWarrantyBattery, CmmBatteryVO cmmBattery, SvWarrantyBO model, SvWarrantyParam param) {

        if (cmmWarrantyBattery == null) {
            cmmWarrantyBattery = new CmmWarrantyBatteryVO();

            cmmWarrantyBattery.setBatteryId(cmmBattery.getBatteryId());
            cmmWarrantyBattery.setSiteId(SITE_666N);
            cmmWarrantyBattery.setProductId(cmmBattery.getProductId());
        }

        cmmWarrantyBattery.setWarrantyProductClassification(param.getWarrantyTypeMap().get(model.getWarrantyPolicyType()));
        cmmWarrantyBattery.setFromDate(model.getWarrantyEffectiveDate());
        cmmWarrantyBattery.setToDate(model.getWarrantyexpiredDate());
        cmmWarrantyBattery.setWarrantyProductUsage(model.getWarrantyMileage());

        param.getUpdWarrantyBattery().add(cmmWarrantyBattery);
    }

    private void buildCmmWarrantySerialProd(CmmWarrantySerializedProductVO cmmWarrantySerialProd, CmmSerializedProductVO cmmSerialProd, SvWarrantyBO model, SvWarrantyParam param) {

        if (cmmWarrantySerialProd == null) {
            cmmWarrantySerialProd = new CmmWarrantySerializedProductVO();
        } else {
            cmmWarrantySerialProd.setProductId(cmmSerialProd.getProductId());
            cmmWarrantySerialProd.setSerializedProductId(cmmSerialProd.getSerializedProductId());
            cmmWarrantySerialProd.setSiteId(SITE_666N);
            cmmWarrantySerialProd.setWarrantySerializedProductId(cmmSerialProd.getSerializedProductId());
        }

        cmmWarrantySerialProd.setFromDate(model.getWarrantyEffectiveDate());
        cmmWarrantySerialProd.setToDate(model.getWarrantyexpiredDate());
        cmmWarrantySerialProd.setWarrantyProductUsage(model.getWarrantyMileage());
        cmmWarrantySerialProd.setWarrantyProductClassification(param.getWarrantyTypeMap().get(model.getWarrantyPolicyType()));

        param.getUpdWarrantySerialProd().add(cmmWarrantySerialProd);
    }

    private void buildSvDemandDtl(CmmServiceDemandDetailVO svDemandDtl, CmmSerializedProductVO cmmSerialProd, Long svDemandId, SvWarrantyItemBO itemBO, SvWarrantyParam param) {

        if (itemBO == null) {
            svDemandDtl = new CmmServiceDemandDetailVO();
            svDemandDtl.setFscResultFlag(N);
        } else {
            svDemandDtl.setFscResultFlag(Y);
            svDemandDtl.setFscResultDate(itemBO.getServiceDate());
            svDemandDtl.setFscUsage(itemBO.getMileage());
        }
        svDemandDtl.setSerializedProductId(cmmSerialProd.getSerializedProductId());
        svDemandDtl.setServiceDemandId(svDemandId);
        svDemandDtl.setSiteId(SITE_666N);
        svDemandDtl.setProductId(cmmSerialProd.getProductId());

        param.getUpdSvDemandDtl().add(svDemandDtl);
    }

    private void prepareMcParams(List<SvWarrantyBO> mcList, SvWarrantyParam param) {

        Map<Long, String> svDemandMap = warrantySer.loadAllSvDemand();
        param.setSvDemandMap(svDemandMap);

        Map<String, SvWarrantyBO> frameNoWithModelMap = mcList.stream().collect(Collectors.toMap(SvWarrantyBO::getFrameNo, Function.identity()));
        Set<String> frameNos = mcList.stream().map(SvWarrantyBO::getFrameNo).collect(Collectors.toSet());
        Set<Long> serialProdIds = new HashSet<>();

        Map<String, CmmSerializedProductVO> cmmSerialProductMap = warrantySer.getSerialProductMap(frameNos);
        for(String frameNo : cmmSerialProductMap.keySet()) {

            CmmSerializedProductVO cmmSerialProd = cmmSerialProductMap.get(frameNo);
            Long serialProdId = cmmSerialProd.getSerializedProductId();
            serialProdIds.add(serialProdId);

            if (!StringUtils.equals(cmmSerialProd.getStockStatus(), McSalesStatus.STOCK)) {
                param.getProdIdAndBOMap().put(serialProdId, frameNoWithModelMap.get(frameNo));
                param.getProdIdAndItemBOMap().put(serialProdId, frameNoWithModelMap.get(frameNo).getResultList());
            }

            param.getProdIdAndSerProdMap().put(serialProdId, cmmSerialProd);
        }

        param.setWarrantyPolicyMap(warrantySer.getWarrantySerialProdMap(serialProdIds));
        param.setSvDemandDtlMap(warrantySer.getSvDemandDtlMap(serialProdIds));
    }
}
