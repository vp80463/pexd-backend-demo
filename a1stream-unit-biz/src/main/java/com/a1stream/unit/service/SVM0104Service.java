package com.a1stream.unit.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SVM010401BO;
import com.a1stream.domain.bo.unit.SVM010402BO;
import com.a1stream.domain.bo.unit.SVM010402ConsumerInfoBO;
import com.a1stream.domain.bo.unit.SVM010402ServiceHistoryBO;
import com.a1stream.domain.bo.unit.SVM010402TransactionHistoryBO;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmConsumer;
import com.a1stream.domain.entity.CmmConsumerSerialProRelation;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.ConsumerPrivateDetail;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.form.unit.SVM010401Form;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmConsumerSerialProRelationRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmServiceHistoryRepository;
import com.a1stream.domain.repository.ConsumerPrivateDetailRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.SerializedProductTranRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConsumerSerialProRelationVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.ymsl.solid.base.util.BeanMapUtils;
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
@Service
public class SVM0104Service {

    @Resource
    private CmmConsumerRepository cmmConsumerRepo;

    @Resource
    private SerializedProductRepository serialProdRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerialProdRepo;

    @Resource
    private CmmConsumerSerialProRelationRepository cmmConsumerSerialProRelaRepo;

    @Resource
    private MstOrganizationRepository mstOrganizationRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private SerializedProductTranRepository serializedProductTranRepo;

    @Resource
    private CmmServiceHistoryRepository cmmServiceHistoryRepo;

    @Resource
    private ServiceOrderRepository serviceOrderRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private BatteryRepository batteryRepo;

    @Resource
    private CmmBatteryRepository cmmBatteryRepo;

    @Resource
    private ConsumerPrivateDetailRepository consumerPrivateDetailRepo;

    public CmmConsumerVO getCmmConsumer(Long consumerId) {

        return BeanMapUtils.mapTo(cmmConsumerRepo.findByConsumerId(consumerId), CmmConsumerVO.class);
    }

    public List<Long> getSerialProductIds(Long consumerId, String mobilephone, String siteId) {

        return cmmConsumerRepo.getSerialProductIds(consumerId, mobilephone, siteId);
    }

    public SVM010402ConsumerInfoBO getMcConsumerById(Long cmmSerializedProductId, Long consumerId, String siteId) {

        return cmmConsumerRepo.getConsumerBasicInfoByConsumerId(cmmSerializedProductId, consumerId, siteId);
    }

    public Map<Long, String> getCmmConsumerMap(List<Long> consumerIds) {

        List<CmmConsumerVO> cmmConsumerList = BeanMapUtils.mapListTo(cmmConsumerRepo.findByConsumerIdIn(consumerIds), CmmConsumerVO.class);

        return cmmConsumerList.stream().collect(Collectors.toMap(CmmConsumerVO::getConsumerId, CmmConsumerVO::getConsumerFullNm));
    }

    public Page<SVM010401BO> getLocalMcData(SVM010401Form form, String siteId) {

        return serialProdRepo.getLocalMcData(form, siteId);
    }

    public Map<Long, String> getProdAndStockStsMap(Set<Long> cmmSerialProIds, String siteId) {

        List<SerializedProductVO> serialProVOList = BeanMapUtils.mapListTo(serialProdRepo.findByCmmSerializedProductIdInAndSiteId(cmmSerialProIds, siteId), SerializedProductVO.class);

        return serialProVOList.stream().collect(Collectors.toMap(SerializedProductVO::getCmmSerializedProductId, item -> item.getStockStatus() != null ? item.getStockStatus() : "", (existing, replacement) -> existing));
    }

    public SerializedProductVO getSerializedProduct(Long cmmSerialProId, String siteId) {

        return BeanMapUtils.mapTo(serialProdRepo.findFirstByCmmSerializedProductIdAndSiteId(cmmSerialProId, siteId), SerializedProductVO.class);
    }

    public Page<SVM010401BO> getCommonMcData(SVM010401Form form, String siteId) {

        return cmmSerialProdRepo.getCommonMcData(form, siteId);
    }

    public Map<Long, String> getProdAndSalesStsMap(Set<Long> cmmSerialProIds) {

        List<CmmSerializedProductVO> cmmSerialProVOList = BeanMapUtils.mapListTo(cmmSerialProdRepo.findBySerializedProductIdIn(cmmSerialProIds), CmmSerializedProductVO.class);

        return cmmSerialProVOList.stream().collect(Collectors.toMap(CmmSerializedProductVO::getSerializedProductId, item -> item.getStockStatus() != null ? item.getSalesStatus() : ""));
    }

    public SVM010402BO getMcBasicInfo(Long cmmSerializedProductId) {

        return cmmSerialProdRepo.getMcBasicInfo(cmmSerializedProductId);
    }

    public CmmSerializedProductVO getCmmSerialProductById(Long cmmSerializedProductId) {

        return BeanMapUtils.mapTo(cmmSerialProdRepo.findBySerializedProductId(cmmSerializedProductId), CmmSerializedProductVO.class);
    }

    public CmmSerializedProductVO getCmmSerialProductByPlate(String plateNo) {

        return BeanMapUtils.mapTo(cmmSerialProdRepo.findFirstByPlateNo(plateNo), CmmSerializedProductVO.class);
    }

    public List<SVM010402ConsumerInfoBO> getMcConsumerData(Long cmmSerializedProductId, String siteId) {

        return cmmConsumerSerialProRelaRepo.getMcConsumerData(cmmSerializedProductId, siteId);
    }

    public SVM010402BO getOwnerConsumerByMc(Long cmmSerializedProductId) {

        return cmmConsumerSerialProRelaRepo.getOwnerConsumerByMc(cmmSerializedProductId);
    }

    public CmmConsumerSerialProRelationVO findConsumerAndMcRelation(Long serializedProId, Long consumerId) {

        return BeanMapUtils.mapTo(cmmConsumerSerialProRelaRepo.findFirstBySerializedProductIdAndConsumerId(serializedProId, consumerId), CmmConsumerSerialProRelationVO.class);
    }

    public CmmConsumerSerialProRelationVO findMcOwnerConsumer(Long serializedProId) {

        return BeanMapUtils.mapTo(cmmConsumerSerialProRelaRepo.findOwnerBySerializedProductId(serializedProId), CmmConsumerSerialProRelationVO.class);
    }
    public ConsumerPrivateDetailVO findConsumerPrivateDetail(Long consumerId, String siteId) {

        return BeanMapUtils.mapTo(consumerPrivateDetailRepo.findFirstByConsumerIdAndSiteId(consumerId, siteId), ConsumerPrivateDetailVO.class);
    }

    public Map<Long, String> getMstOrganizationVOList(Set<Long> orgIds) {

        List<MstOrganizationVO> toMstOrganizationList = BeanMapUtils.mapListTo(mstOrganizationRepo.findByOrganizationIdIn(orgIds), MstOrganizationVO.class);

        return toMstOrganizationList.stream().collect(Collectors.toMap(MstOrganizationVO::getOrganizationId, MstOrganizationVO::getOrganizationCd));
    }

    public Map<Long, String> getMstFacilityVOList(Set<Long> facilityIds) {

        List<MstFacilityVO> toMstFacilityList = BeanMapUtils.mapListTo(mstFacilityRepo.findByFacilityIdIn(facilityIds), MstFacilityVO.class);

        return toMstFacilityList.stream().collect(Collectors.toMap(MstFacilityVO::getFacilityId, MstFacilityVO::getFacilityCd));
    }

    public List<SVM010402TransactionHistoryBO> getMcTransHistData(Long cmmSerializedProductId, String siteId) {

        return serializedProductTranRepo.getMcTransHistData(cmmSerializedProductId, siteId);
    }

    public List<SVM010402ServiceHistoryBO> getMcServiceHistData(Long cmmSerializedProductId) {

        return cmmServiceHistoryRepo.getMcServiceHistData(cmmSerializedProductId);
    }

    public List<ServiceOrderVO> getServiceOrderVOList(List<String> frameNoList) {

        return BeanMapUtils.mapListTo(serviceOrderRepo.findByFrameNoIn(frameNoList), ServiceOrderVO.class);
    }

    public Map<String, BatteryVO> findBatteryNoMap(Set<String> batteryNos) {

        List<BatteryVO> resultVO = BeanMapUtils.mapListTo(batteryRepo.findByBatteryNoIn(batteryNos), BatteryVO.class);

        return resultVO.stream().collect(Collectors.toMap(BatteryVO::getBatteryNo, Function.identity()));
    }

    public Map<String, CmmBatteryVO> findCmmBatteryNoMap(Set<String> batteryNos) {

        List<CmmBatteryVO> resultVO = BeanMapUtils.mapListTo(cmmBatteryRepo.findByBatteryNoIn(batteryNos), CmmBatteryVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmBatteryVO::getBatteryNo, Function.identity()));
    }

    public List<MstProductVO> getMstProductList(Set<String> productCdSet, String productClassification) {

        return BeanMapUtils.mapListTo(mstProductRepo.findByProductCdInAndProductClassification(productCdSet, productClassification), MstProductVO.class);
    }

    public void updateConsumerInfo(CmmConsumerVO cmmConsumer, ConsumerPrivateDetailVO consumerPrivateDetail, List<CmmConsumerSerialProRelationVO> consumerAndMcRelations) {

        if (!Nulls.isNull(cmmConsumer)) {
            cmmConsumerRepo.save(BeanMapUtils.mapTo(cmmConsumer, CmmConsumer.class));
        }
        if (!Nulls.isNull(consumerPrivateDetail)) {
            consumerPrivateDetailRepo.save(BeanMapUtils.mapTo(consumerPrivateDetail, ConsumerPrivateDetail.class));
        }
        if (!consumerAndMcRelations.isEmpty()) {
            cmmConsumerSerialProRelaRepo.saveInBatch(BeanMapUtils.mapListTo(consumerAndMcRelations, CmmConsumerSerialProRelation.class));
        }
    }

    public void maintainMcData(CmmSerializedProductVO cmmSerialProduct
                            , SerializedProductVO serialProduct
                            , List<CmmBatteryVO> updCmmBatteryList
                            , List<BatteryVO> updBatteryList) {

        cmmSerialProdRepo.save(BeanMapUtils.mapTo(cmmSerialProduct, CmmSerializedProduct.class));
        serialProdRepo.save(BeanMapUtils.mapTo(serialProduct, SerializedProduct.class));
        cmmBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(updCmmBatteryList, CmmBattery.class));
        batteryRepo.saveInBatch(BeanMapUtils.mapListTo(updBatteryList, Battery.class));
    }
}
