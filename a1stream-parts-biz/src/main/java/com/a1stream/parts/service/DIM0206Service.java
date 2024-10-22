package com.a1stream.parts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.entity.ProductCost;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.ProductStockStatusBackup;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusBackupRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusBackupVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Stock Import
*
* @author mid2178
*/
@Service
public class DIM0206Service {

    @Resource
    private SystemParameterRepository systemParameterRepo;

    @Resource
    private SalesOrderRepository salesOrderRepo;

    @Resource
    private ReceiptSlipRepository receiptSlipRepo;

    @Resource
    private LocationRepository locationRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private ProductStockStatusBackupRepository stockStatusBackupRepo;

    @Resource
    private ProductStockStatusRepository proStockStatusRepo;

    @Resource
    private ProductCostRepository productCostRepo;

    public SystemParameterVO getProcessingSystemParameter(String siteId, Long facilityId, String paramTypeId, String paramValue) {

        return BeanMapUtils.mapTo(systemParameterRepo.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue), SystemParameterVO.class);
    }

    public Integer countSalesOrder(Long facilityId, String siteId) {

        return salesOrderRepo.countSalesOrder(facilityId, siteId);
    }

    public Integer countServiceOrder(Long facilityId, String siteId) {

        return salesOrderRepo.countServiceOrder(facilityId, siteId);
    }

    public Integer countPartsStoring(Long facilityId, String siteId) {

        return receiptSlipRepo.countPartsStoring(facilityId, siteId);
    }

    public List<MstFacilityVO> getFacilityByCd(Set<String> facilityCds, String siteId) {

        return BeanMapUtils.mapListTo(mstFacilityRepo.findBySiteIdAndFacilityCdIn(siteId, facilityCds), MstFacilityVO.class);
    }

    public List<LocationVO> getLocByCds(String siteId, Long facilityId, Set<String> locationCds) {

        return BeanMapUtils.mapListTo(locationRepo.findBySiteIdAndFacilityIdAndLocationCdIn(siteId, facilityId, new ArrayList<>(locationCds)), LocationVO.class);
    }

    public List<MstProductVO> getProductByCds(Set<String> productCds, List<String> siteList) {

        return BeanMapUtils.mapListTo(mstProductRepo.getProductByCds(new ArrayList<>(productCds), siteList, ProductClsType.PART.getCodeDbid()), MstProductVO.class);
    }

    public Integer countStockLine(String siteId, Long facilityId) {

        return productInventoryRepo.countBySiteIdAndFacilityId(siteId, facilityId);
    }

    public List<ProductStockStatusBackupVO> getStockStatusBK(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(stockStatusBackupRepo.findBysitePoint(siteId, facilityId, ProductClsType.PART.getCodeDbid()), ProductStockStatusBackupVO.class);
    }

    public void saveInDB(String siteId
                       , Long facilityId
                       , Set<String> statusTypes
                       , List<ProductStockStatusBackupVO> stockStatusBkDel
                       , List<ProductStockStatusVO> pStatusDel
                       , List<ProductInventoryVO> proInvUpdateList
                       , List<ProductStockStatusVO> proStatusUpdateList
                       , List<ProductCostVO> proCostUpdateList
                       , List<ProductInventoryVO> proInvVos) {

        // delete backup
        stockStatusBackupRepo.deleteAllInBatch(BeanMapUtils.mapListTo(stockStatusBkDel, ProductStockStatusBackup.class));

        // copy stockStatus to backup
        stockStatusBackupRepo.insertCopy(siteId, facilityId, statusTypes);

        // delete ProductStockStatus
        if (!ObjectUtils.isEmpty(pStatusDel)) {
            proStockStatusRepo.deleteAll(BeanMapUtils.mapListTo(pStatusDel, ProductStockStatus.class));
        }

        // insert or update ProductInventory
        productInventoryRepo.saveInBatch(BeanMapUtils.mapListTo(proInvUpdateList, ProductInventory.class));

        // insert stockStatus
        proStockStatusRepo.saveInBatch(BeanMapUtils.mapListTo(proStatusUpdateList, ProductStockStatus.class));

        // insert or update productCost
        productCostRepo.saveInBatch(BeanMapUtils.mapListTo(proCostUpdateList, ProductCost.class));

        // copy backup to stockStatus
        proStockStatusRepo.insertFromBK(siteId, facilityId, statusTypes);

        // set zero
        productInventoryRepo.saveInBatch(BeanMapUtils.mapListTo(proInvVos, ProductInventory.class));

    }

    public List<ProductInventoryVO> getProInvVOs(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(productInventoryRepo.getPartBysitePoint(siteId, facilityId, ProductClsType.PART.getCodeDbid()), ProductInventoryVO.class);
    }

    public List<ProductStockStatusVO> getProStatusVOs(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(proStockStatusRepo.getPartStockStatus(siteId, facilityId, ProductClsType.PART.getCodeDbid()), ProductStockStatusVO.class);
    }

    public Map<Long, String> getlocTypeByIds(Set<Long> locationIds) {

        return locationRepo.getlocTypeByIds(locationIds);
    }

    public List<ProductCostVO> getProCostVos(String siteId, Set<Long> productIds) {

        return BeanMapUtils.mapListTo(productCostRepo.findByProductIdInAndCostTypeAndSiteId(productIds, CostType.AVERAGE_COST, siteId), ProductCostVO.class);
    }

    public MstFacilityVO getFacilityById(Long pointId) {

        return BeanMapUtils.mapTo(mstFacilityRepo.findByFacilityId(pointId), MstFacilityVO.class);
    }
}
