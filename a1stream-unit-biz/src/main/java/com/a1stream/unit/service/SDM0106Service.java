package com.a1stream.unit.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.CmmUnitPromotionItem;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.ProductCost;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.ReceiptSerializedItem;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.entity.SerializedProductTran;
import com.a1stream.domain.form.unit.SDM010601Form;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmUnitPromotionItemRepository;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ReceiptSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.SerializedProductTranRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReceiptSerializedItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SerializedProductTranVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Receipt Report
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/09/03   Wang Nan      New
*/
@Service
public class SDM0106Service {

    @Resource
    private ReceiptSlipRepository receiptSlipRepo;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepo;

    @Resource
    private ReceiptSerializedItemRepository receiptSerializedItemRepo;

    @Resource
    private ProductCostRepository productCostRepo;

    @Resource
    private ProductStockStatusRepository productStockStatusRepo;

    @Resource
    private SerializedProductRepository serializedProductRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;

    @Resource
    private BatteryRepository batteryRepo;

    @Resource
    private CmmBatteryRepository cmmBatteryRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepo;

    @Resource
    private CmmUnitPromotionItemRepository cmmUnitPromotionItemRepo;

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepo;

    @Resource
    private SerializedProductTranRepository serializedProductTranRepo;

    @Resource
    private ConstantsLogic constantsLogic;

    public List<SDM010601BO> getFastReceiptReportList(SDM010601Form form) {
        return receiptSlipRepo.getFastReceiptReportList(form);
    }

    public List<ReceiptSlipVO> getReceiptSlipVOList(Set<Long> receiptSlipIds) {
        return BeanMapUtils.mapListTo(receiptSlipRepo.findByReceiptSlipIdIn(receiptSlipIds), ReceiptSlipVO.class);
    }

    public List<ReceiptSlipItemVO> getReceiptSlipItemVOList(Set<Long> receiptSlipIds) {
        return BeanMapUtils.mapListTo(receiptSlipItemRepo.findByReceiptSlipIdIn(receiptSlipIds), ReceiptSlipItemVO.class);
    }

    public List<ReceiptSerializedItemVO> getReceiptSerializedItemVOList(Set<Long> receiptSlipIds) {
        return BeanMapUtils.mapListTo(receiptSerializedItemRepo.findByReceiptSlipIdIn(receiptSlipIds), ReceiptSerializedItemVO.class);
    }

    public List<ProductCostVO> getProductCostVOList(Set<Long> productIds, String costType) {
        return BeanMapUtils.mapListTo(productCostRepo.findByProductIdInAndCostType(productIds, costType), ProductCostVO.class);
    }

    public List<SerializedProductVO> getSerializedProductVOList(Set<Long> serializedProductIds, Long facilityId) {
        return BeanMapUtils.mapListTo(serializedProductRepo.findBySerializedProductIdInAndFacilityId(serializedProductIds, facilityId), SerializedProductVO.class);
    }

    public List<CmmSerializedProductVO> getCmmSerializedProductVOList(Set<Long> cmmSerialProIds) {
        return BeanMapUtils.mapListTo(cmmSerializedProductRepo.findBySerializedProductIdIn(cmmSerialProIds), CmmSerializedProductVO.class);
    }

    public List<CmmSerializedProductVO> getCmmSerializedProductVOList(Set<Long> serializedProductIds, String salesStatus) {
        return BeanMapUtils.mapListTo(cmmSerializedProductRepo.findBySerializedProductIdInAndSalesStatus(serializedProductIds, salesStatus), CmmSerializedProductVO.class);
    }

    public List<ProductStockStatusVO> getProductStockStatusVO(String siteId, Long facilityId, Set<Long> productIds, String productStockStatusType) {
        return BeanMapUtils.mapListTo(productStockStatusRepo.findProductStockStatusList(siteId, facilityId, productIds, productStockStatusType), ProductStockStatusVO.class);
    }

    public List<MstProductVO> getByProductIdIn(Set<Long> partsIds) {
        return BeanMapUtils.mapListTo(mstProductRepo.findByProductIdIn(partsIds), MstProductVO.class);
    }

    public List<BatteryVO> getBatteryVOList(Set<Long> serializedProductIds, String sysDate) {
        return BeanMapUtils.mapListTo(batteryRepo.findBatteryVOList(serializedProductIds, sysDate), BatteryVO.class);
    }

    public List<CmmBatteryVO> getCmmBatteryVOList(List<Long> batteryIdList) {
        return BeanMapUtils.mapListTo(cmmBatteryRepo.findByBatteryIdIn(batteryIdList), CmmBatteryVO.class);
    }

    public List<SDM010601BO> getEffectivePromotionInfoList(String sysDate) {
        return cmmUnitPromotionListRepo.getEffectivePromotionInfoList(sysDate);
    }

    public List<SDM010601BO> getPromotionInfoByFrameNoList(List<String> frameNos) {
        return cmmUnitPromotionItemRepo.getPromotionInfoByFrameNoList(frameNos);
    }

    public List<CmmUnitPromotionItemVO> getCmmUnitPromotionItemVOList(List<Long> unitPromotionItemIds) {
        return BeanMapUtils.mapListTo(cmmUnitPromotionItemRepo.findByUnitPromotionItemIdIn(unitPromotionItemIds), CmmUnitPromotionItemVO.class);
    }

    public Map<String, String> getCodeMstS027(String codeId) {

        Map<String, Field[]> constantsFieldMap =  PJConstants.ConstantsFieldMap;
        List<ConstantsBO> constants = constantsLogic.getConstantsData(constantsFieldMap.get(codeId));
        Map<String, String> mstCodeS027Map = new HashMap<>();

        for(ConstantsBO item : constants) {
            mstCodeS027Map.put(item.getCodeDbid(), item.getCodeData2());
        }
        return mstCodeS027Map;
    }

    public void saveOrUpdate(List<ReceiptSerializedItemVO> receiptSerializedItemVOList,
                             List<ReceiptSlipVO> receiptSlipVOList,
                             List<ProductCostVO> productCostVOList,
                             List<ProductStockStatusVO> productStockStatusVOList,
                             List<InventoryTransactionVO> inventoryTransactionVOList,
                             List<SerializedProductVO> serializedProductVOList,
                             List<CmmSerializedProductVO> cmmSerializedProductVOList,
                             List<BatteryVO> batteryVOList,
                             List<CmmBatteryVO> cmmBatteryVOList,
                             List<SerializedProductTranVO> serializedProductTranVOList,
                             List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList) {

        receiptSerializedItemRepo.saveInBatch(BeanMapUtils.mapListTo(receiptSerializedItemVOList, ReceiptSerializedItem.class));
        receiptSlipRepo.saveInBatch(BeanMapUtils.mapListTo(receiptSlipVOList, ReceiptSlip.class));
        productCostRepo.saveInBatch(BeanMapUtils.mapListTo(productCostVOList, ProductCost.class));
        productStockStatusRepo.saveInBatch(BeanMapUtils.mapListTo(productStockStatusVOList, ProductStockStatus.class));
        inventoryTransactionRepo.saveInBatch(BeanMapUtils.mapListTo(inventoryTransactionVOList, InventoryTransaction.class));
        serializedProductRepo.saveInBatch(BeanMapUtils.mapListTo(serializedProductVOList, SerializedProduct.class));
        cmmSerializedProductRepo.saveInBatch(BeanMapUtils.mapListTo(cmmSerializedProductVOList, CmmSerializedProduct.class));
        batteryRepo.saveInBatch(BeanMapUtils.mapListTo(batteryVOList, Battery.class));
        cmmBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(cmmBatteryVOList, CmmBattery.class));
        serializedProductTranRepo.saveInBatch(BeanMapUtils.mapListTo(serializedProductTranVOList, SerializedProductTran.class));
        cmmUnitPromotionItemRepo.saveInBatch(BeanMapUtils.mapListTo(cmmUnitPromotionItemVOList, CmmUnitPromotionItem.class));
    }
}
