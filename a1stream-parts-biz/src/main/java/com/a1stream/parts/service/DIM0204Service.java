package com.a1stream.parts.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.DIM020401BO;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.CmmSpecialClaimSerialPro;
import com.a1stream.domain.entity.Location;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.ProductStockTaking;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.entity.StoringLine;
import com.a1stream.domain.entity.StoringLineItem;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ProductStockTakingRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Unfinished Order Cancel
*
* @author mid2178
*/
@Service
public class DIM0204Service {

    @Resource
    private SystemParameterRepository systemParameterRepo;

    @Resource
    private LocationRepository locationRepo;

    @Resource
    private ProductStockStatusRepository productStockStatusRepo;

    @Resource
    private ProductStockTakingRepository productStockTakingRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    @Resource
    private SalesOrderRepository salesOrderRepo;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;

    @Resource
    private ServiceOrderRepository serviceOrderRepo;

    @Resource
    private CmmSpecialClaimSerialProRepository specialClaimSerialProRepo;

    @Resource
    private StoringLineRepository storingLineRepo;

    @Resource
    private StoringLineItemRepository storingLineItemRepo;

    @Resource
    private SerializedProductRepository serializedProductRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;

    public SystemParameterVO getProcessingSystemParameter(String siteId, Long facilityId, String paramTypeId, String paramValue) {

        return BeanMapUtils.mapTo(systemParameterRepo.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue),SystemParameterVO.class);
    }

    public List<ProductStockTakingVO> getProductStockTakingInfo(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(productStockTakingRepo.findBySiteIdAndFacilityId(siteId, facilityId), ProductStockTakingVO.class);
    }

    public void doCancelData(SystemParameterVO sysParamUpdate, List<ProductStockTakingVO> proStoTakDel) {

        if(sysParamUpdate != null) {
            systemParameterRepo.save(BeanMapUtils.mapTo(sysParamUpdate,SystemParameter.class));
        }
        if(!proStoTakDel.isEmpty()) {
            productStockTakingRepo.deleteAllInBatch(BeanMapUtils.mapListTo(proStoTakDel,ProductStockTaking.class));
        }
    }

    public List<DIM020401BO> getSparePartsDownloadInfo(Long facilityId, String siteId) {

        return salesOrderRepo.getSparePartsDownloadInfo(facilityId, siteId);
    }

    public List<DIM020401BO> getServiceDownloadInfo(Long facilityId, String siteId) {

        return salesOrderRepo.getServiceDownloadInfo(facilityId, siteId);
    }

    public List<DIM020401BO> getPartsStoringDownloadInfo(Long facilityId, String siteId) {

        return salesOrderRepo.getPartsStoringDownloadInfo(facilityId, siteId);
    }

    public List<SalesOrderItemVO> getCancelSalesOrderItems(Set<Long> salesOrderIds, String siteId) {

        return BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderIds(siteId, salesOrderIds), SalesOrderItemVO.class);
    }

    public void saveSparePartsCancel(List<SalesOrderItemVO> itemVOs) {

        salesOrderItemRepo.deleteAllInBatch(BeanMapUtils.mapListTo(itemVOs,SalesOrderItem.class));
    }

    public List<ServiceOrderVO> getCancelSVList(Set<Long> salesOrderIds, String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(serviceOrderRepo.getByRelatedSalesOrderIds( siteId, facilityId, salesOrderIds), ServiceOrderVO.class);
    }

    public List<CmmSpecialClaimSerialProVO> getByClaimIds(String siteId, String facilityCd, String claimFlag, Set<String> frameNos, Set<Long> claimIds) {

        return BeanMapUtils.mapListTo(specialClaimSerialProRepo.getByClaimIds(siteId, facilityCd, claimFlag, frameNos, claimIds), CmmSpecialClaimSerialProVO.class);
    }

    public List<SalesOrderVO> findBySalesOrderIdIn(Set<Long> salesOrderIds) {

        return BeanMapUtils.mapListTo(salesOrderRepo.findBySalesOrderIdIn(salesOrderIds), SalesOrderVO.class);
    }

    public List<StoringLineVO> findByStoringLineIdIn(Set<Long> storingLineIds) {

        return BeanMapUtils.mapListTo(storingLineRepo.findByStoringLineIdIn(storingLineIds), StoringLineVO.class);
    }

    public List<StoringLineItemVO> getByStoringLineIdIn(String siteId, Set<Long> storingLineIds) {

        return BeanMapUtils.mapListTo(storingLineItemRepo.findBySiteIdAndStoringLineIdIn(siteId, storingLineIds), StoringLineItemVO.class);
    }

    public void doLocationDelete(List<ProductStockStatusVO> proStocStatusDel, List<ProductInventoryVO> proInvDel, List<LocationVO> locationDel) {

        //库存表数据删除
        productStockStatusRepo.deleteAllInBatch(BeanMapUtils.mapListTo(proStocStatusDel, ProductStockStatus.class));

        // 库存库位表数据删除
        productInventoryRepo.deleteAllInBatch(BeanMapUtils.mapListTo(proInvDel, ProductInventory.class));

        // 库位基础数据删除
        locationRepo.deleteAllInBatch(BeanMapUtils.mapListTo(locationDel, Location.class));
    }

    public void doPartsStoringCancel(List<StoringLineVO> storingLineList, List<StoringLineItemVO> storingLineItemList) {

        storingLineRepo.saveInBatch(BeanMapUtils.mapListTo(storingLineList, StoringLine.class));
        storingLineItemRepo.saveInBatch(BeanMapUtils.mapListTo(storingLineItemList, StoringLineItem.class));
    }

    public List<SerializedProductVO> getSerialPro(Set<Long> cmmSerialProIds) {

        return BeanMapUtils.mapListTo(serializedProductRepo.findByCmmSerializedProductIdIn(cmmSerialProIds), SerializedProductVO.class);
    }

    public List<CmmSerializedProductVO> getcmmSerialPro(Set<Long> cmmSerialProIds) {

        return BeanMapUtils.mapListTo(cmmSerializedProductRepo.findBySerializedProductIdIn(cmmSerialProIds), CmmSerializedProductVO.class);
    }

    public void doServiceCancel(List<ServiceOrderVO> serviceOrderList
                              , List<SalesOrderVO> salesOrderList
                              , List<CmmSpecialClaimSerialProVO> claimSerialProList
                              , List<SerializedProductVO> serialProVOs
                              , List<CmmSerializedProductVO> cmmSerialProVOs) {

        // 取消服务单
        serviceOrderRepo.saveInBatch(BeanMapUtils.mapListTo(serviceOrderList, ServiceOrder.class));

        // 取消销售单
        salesOrderRepo.saveInBatch(BeanMapUtils.mapListTo(salesOrderList, SalesOrder.class));

        // 取消召回
        specialClaimSerialProRepo.saveInBatch(BeanMapUtils.mapListTo(claimSerialProList, CmmSpecialClaimSerialPro.class));

        // 更新车台品质状态为正常
        serializedProductRepo.saveInBatch(BeanMapUtils.mapListTo(serialProVOs, SerializedProduct.class));
        cmmSerializedProductRepo.saveInBatch(BeanMapUtils.mapListTo(cmmSerialProVOs, CmmSerializedProduct.class));
    }

    public List<ProductStockStatusVO> getProStocStatus(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(productStockStatusRepo.getPartStockStatus(siteId, facilityId, ProductClsType.PART.getCodeDbid()), ProductStockStatusVO.class);
    }

    public List<ProductInventoryVO> getProInventory(String siteId, Long facilityId) {

        return productInventoryRepo.getProInventoryToDel(siteId, facilityId);
    }

    public List<LocationVO> getLocation(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(locationRepo.findBySiteIdAndFacilityId(siteId, facilityId), LocationVO.class);
    }
}
