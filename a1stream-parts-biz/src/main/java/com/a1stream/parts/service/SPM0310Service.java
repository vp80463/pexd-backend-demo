package com.a1stream.parts.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM031001BO;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.ProductStockTaking;
import com.a1stream.domain.entity.ProductStockTakingHistory;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ProductStockTakingHistoryRepository;
import com.a1stream.domain.repository.ProductStockTakingRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ProductStockTakingHistoryVO;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:cancel or finish stockTaking
*
* @author mid2178
*/
@Service
public class SPM0310Service {

    @Resource
    private ProductStockTakingRepository productStockTakingRepo;

    @Resource
    private ProductStockTakingHistoryRepository productStockTakingHistoryRepo;

    @Resource
    private SystemParameterRepository systemParameterRepo;

    @Resource
    private ProductStockStatusRepository productStockStatusRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    public Map<String,SPM031001BO> getProductStockTakingByType(String siteId, Long facilityId, String productClassification) {

        return productStockTakingRepo.getProductStockTakingByType(siteId, facilityId, productClassification);
    }

    public List<ProductStockTakingVO> getStockTakingProcessing(String siteId, Long facilityId, String productClassification) {

        return BeanMapUtils.mapListTo(productStockTakingRepo.findBySiteIdAndFacilityIdAndProductClassification(siteId, facilityId, productClassification), ProductStockTakingVO.class);
    }

    public SystemParameterVO getProcessingSystemParameter(String siteId, Long facilityId, String paramTypeId, String paramValue) {

        return BeanMapUtils.mapTo(systemParameterRepo.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue),SystemParameterVO.class);
    }
    public List<ProductStockTakingVO> getProductStockTakingInfo(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(productStockTakingRepo.findBySiteIdAndFacilityId(siteId, facilityId), ProductStockTakingVO.class);
    }

    public void doCancelData(SystemParameterVO systemParameterUpdate, List<ProductStockTakingVO> productStockTakingDelete) {

        if(systemParameterUpdate != null) {
            systemParameterRepo.save(BeanMapUtils.mapTo(systemParameterUpdate,SystemParameter.class));
        }

        if(!productStockTakingDelete.isEmpty()) {
            productStockTakingRepo.deleteAllInBatch(BeanMapUtils.mapListTo(productStockTakingDelete,ProductStockTaking.class));
        }
    }

    public void doFinishData(SystemParameterVO sysParam
                           , List<ProductStockTakingVO> proStoTakList
                           , ProductStockTakingHistoryVO proStoTakHistory
                           , List<ProductStockStatusVO> proStoStatusList
                           , List<ProductInventoryVO> proInvList
                           , List<InventoryTransactionVO> invTranList) {

        if(sysParam != null) {
            systemParameterRepo.save(BeanMapUtils.mapTo(sysParam,SystemParameter.class));
        }
        if(proStoTakHistory != null) {
            productStockTakingHistoryRepo.save(BeanMapUtils.mapTo(proStoTakHistory,ProductStockTakingHistory.class));
        }
        productStockTakingRepo.saveInBatch(BeanMapUtils.mapListTo(proStoTakList,ProductStockTaking.class));
        productStockStatusRepo.saveInBatch(BeanMapUtils.mapListTo(proStoStatusList,ProductStockStatus.class));
        productInventoryRepo.saveInBatch(BeanMapUtils.mapListTo(proInvList,ProductInventory.class));
        inventoryTransactionRepo.saveInBatch(BeanMapUtils.mapListTo(invTranList,InventoryTransaction.class));
    }

    public List<SPM031001BO> getStockTakingSummary(String siteId, Long facilityId) {

        return productStockTakingRepo.getStockTakingSummary(siteId, facilityId);
    }

    public List<ProductStockStatusVO> getProductStockStatus(String siteId, Long facilityId, Set<Long> productIds, Set<String> productStockStatusTypes) {

        return BeanMapUtils.mapListTo(productStockStatusRepo.findStockStatusList(siteId
                                                                               , facilityId
                                                                               , productIds
                                                                               , ProductClsType.PART.getCodeDbid()
                                                                               , productStockStatusTypes), ProductStockStatusVO.class);
    }

    public List<SPM031001BO> getProductStockTakingList(String siteId, Long facilityId) {

        return productStockTakingRepo.getProductStockTakingList(siteId, facilityId);
    }

    public List<ProductInventoryVO> findByProductInventoryIdIn(Set<Long> productInventoryIds) {

        return BeanMapUtils.mapListTo(productInventoryRepo.findByProductInventoryIdIn(productInventoryIds), ProductInventoryVO.class);
    }

    public Map<String,SPM031001BO> getPrintPartsStocktakingResultList(Long facilityId, String siteId) {
        return productStockTakingRepo.getPrintPartsStocktakingResultList(facilityId, siteId);
    }

    public List<SPM031001BO> getPartsStocktakingResultStatisticsList(String siteId, Long facilityId) {

        return productStockTakingRepo.getPartsStocktakingResultStatisticsList(siteId, facilityId);
    }

    public List<SPM031001BO> getPrintPartsStocktakingGapList(String siteId, Long facilityId) {

        return productStockTakingRepo.getPrintPartsStocktakingGapList(siteId, facilityId);
    }

    public List<SPM031001BO> getPrintPartsStocktakingLedger(String siteId, Long facilityId) {

        return productStockTakingRepo.getPrintPartsStocktakingLedgerList(siteId, facilityId);
    }

    public MstFacilityVO getFacilityCdAndFacilityNm(Long facilityId) {

        return BeanMapUtils.mapTo(mstFacilityRepo.findByFacilityId(facilityId), MstFacilityVO.class);
    }
}