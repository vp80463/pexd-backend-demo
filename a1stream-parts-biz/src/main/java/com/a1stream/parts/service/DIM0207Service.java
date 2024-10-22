package com.a1stream.parts.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.entity.Location;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.repository.BinTypeRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.repository.WorkzoneRepository;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstCodeInfoVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Location Import
*
* @author mid2178
*/
@Service
public class DIM0207Service {

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
    private WorkzoneRepository workzoneRepo;

    @Resource
    private BinTypeRepository binTypeRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private MstCodeInfoRepository mstCodeInfoRepo;

    @Resource
    private InventoryManager inventoryManager;

    public List<ProductInventoryVO> findMainProductInventoryList(String siteId, Set<Long> productIds, Long pointId,String mainFlag){

        return  BeanMapUtils.mapListTo(productInventoryRepo.findMainProductInventoryList(siteId, productIds, pointId,mainFlag), ProductInventoryVO.class);
    }

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

    public void saveInDB(List<LocationVO> locationList, List<ProductInventoryVO> proInventoryList,Set<Long> beforeLocationIds) {

        locationRepo.saveInBatch(BeanMapUtils.mapListTo(locationList, Location.class));
        productInventoryRepo.saveInBatch(BeanMapUtils.mapListTo(proInventoryList, ProductInventory.class));
        inventoryManager.doUpdateLocationMainFlag(beforeLocationIds, new HashSet<>());
    }

    public List<MstFacilityVO> getFacilityByCd(Set<String> facilityCds, String siteId) {

        return BeanMapUtils.mapListTo(mstFacilityRepo.findBySiteIdAndFacilityCdIn(siteId, facilityCds), MstFacilityVO.class);
    }

    public List<WorkzoneVO> getWzByCds(Set<String> workzoneCds, String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(workzoneRepo.findBySiteIdAndFacilityIdAndWorkzoneCdIn(siteId,facilityId, workzoneCds), WorkzoneVO.class);
    }

    public List<BinTypeVO> getBinTypeByDescription(Set<String> binTypes, String siteId) {

        return BeanMapUtils.mapListTo(binTypeRepo.findBySiteIdAndDescriptionIn(siteId, binTypes), BinTypeVO.class);
    }

    public List<LocationVO> getLocByCds(String siteId, Long facilityId, Set<String> locationCds) {

        return BeanMapUtils.mapListTo(locationRepo.findBySiteIdAndFacilityIdAndLocationCdIn(siteId, facilityId, new ArrayList<>(locationCds)), LocationVO.class);
    }

    public List<MstProductVO> getProductByCds(Set<String> productCds, List<String> siteList) {

        return BeanMapUtils.mapListTo(mstProductRepo.getProductByCds(new ArrayList<>(productCds), siteList, ProductClsType.PART.getCodeDbid()), MstProductVO.class);
    }

    public List<MstCodeInfoVO> getLocationType(String codeId, Set<String> locationTypes) {

        return BeanMapUtils.mapListTo(mstCodeInfoRepo.findByCodeIdAndCodeData1In(codeId, locationTypes), MstCodeInfoVO.class);
    }

    public Integer countStockLine(String siteId, Long facilityId) {

        return productInventoryRepo.countBySiteIdAndFacilityId(siteId, facilityId);
    }

    public Map<String, ProductInventoryVO> getProInvByCds(Set<String> locationCds, Set<String> productCds, Long facilityId, String siteId) {

        return productInventoryRepo.getProInvByCds(locationCds, productCds, facilityId, siteId);
    }

    public MstFacilityVO getFacilityById(Long pointId) {

        return BeanMapUtils.mapTo(mstFacilityRepo.findByFacilityId(pointId), MstFacilityVO.class);
    }

    public List<ProductInventoryVO> getMainProductInventoryVOs(Long locationId){

        return BeanMapUtils.mapListTo(productInventoryRepo.findByLocationIdAndPrimaryFlag(locationId,CommonConstants.CHAR_Y), ProductInventoryVO.class);
    }
}
