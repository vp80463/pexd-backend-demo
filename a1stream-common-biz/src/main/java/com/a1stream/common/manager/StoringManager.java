package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.LocationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.StoringLine;
import com.a1stream.domain.entity.StoringLineItem;
import com.a1stream.domain.entity.StoringList;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.repository.StoringListRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.domain.vo.StoringListVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class StoringManager {

    @Resource
    private GenerateNoManager generateNoMgr;

    @Resource
    private InventoryManager inventoryMgr;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepo;

    @Resource
    private StoringListRepository storingListRepo;

    @Resource
    private StoringLineRepository storingLineRepo;

    @Resource
    private StoringLineItemRepository storingLineItemRepo;

    @Resource
    private LocationRepository locationRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    /**
    *
    * doSalesReturn mid1459 2024年6月26日
    * @param storingPointId
    * @param receiptSlipInfos
    */
   public List<Long> doSalesReturn(Long storingPointId
                                 , List<ReceiptSlipVO> receiptSlipInfos) {

       List<Long> resultList = new ArrayList<Long>();

       // 1 check arg
       chkArguments(storingPointId, receiptSlipInfos);

       // 2 create storingList list
       // Key : receiptSlipId Value : storingListId
       Map<Long, Long> storingListIdMap = new HashMap<>();
       List<StoringListVO> storingListInfos = createStoringList(storingPointId
                                                              , InventoryTransactionType.RETURNIN.getCodeDbid()
                                                              , receiptSlipInfos
                                                              , storingListIdMap
                                                              , resultList);

       // Key : receiptSlipId Value : receiptSlip
       Map<Long, ReceiptSlipVO> receiptSlipMap = receiptSlipInfos.stream().collect(Collectors.toMap(ReceiptSlipVO::getReceiptSlipId, receiptSlipVO -> receiptSlipVO));
       Map<Long, ReceiptSlipItemVO> receiptSlipItemMap = getReceiptSlipItemMap(receiptSlipMap);

       // 3 create storingLine list
       List<StoringLineVO> storingLineInfos = createStoringLineInfos(storingPointId
                                                                   , receiptSlipMap
                                                                   , receiptSlipItemMap
                                                                   , storingListIdMap);

       // 4 create storingLineItem list
       List<StoringLineItemVO> storingLineItemInfos = createStoringLineItemInfosForSalesReturn(storingLineInfos
                                                                                             , receiptSlipItemMap);

       // 5 commit storingList \ storingLine \ storingLineItem
       storingListRepo.saveInBatch(BeanMapUtils.mapListTo(storingListInfos,StoringList.class));
       storingLineRepo.saveInBatch(BeanMapUtils.mapListTo(storingLineInfos,StoringLine.class));
       storingLineItemRepo.saveInBatch(BeanMapUtils.mapListTo(storingLineItemInfos,StoringLineItem.class));

       return resultList;
    }

    private List<StoringListVO> createStoringList(Long storingPointId
                                                , String inventoryTransactionType
                                                , List<ReceiptSlipVO> receiptSlipInfos
                                                , Map<Long, Long> storingListIdMap
                                                , List<Long> resultList) {

        List<StoringListVO> storingListInfos = new ArrayList<StoringListVO>();

        StoringListVO storingListInfo = null;
        for (ReceiptSlipVO receiptSlip : receiptSlipInfos) {

            storingListInfo = createStoringListByReceiptSlip(storingPointId
                                                           , inventoryTransactionType
                                                           , receiptSlip);
            resultList.add(storingListInfo.getStoringListId());
            storingListInfos.add(storingListInfo);

            storingListIdMap.put(receiptSlip.getReceiptSlipId(), storingListInfo.getStoringListId());
        }

        return storingListInfos;
    }

    private Map<Long, ReceiptSlipItemVO> getReceiptSlipItemMap(Map<Long, ReceiptSlipVO> receiptSlipMap) {

        Set<Long> receiptSlipIds = receiptSlipMap.keySet();
        List<ReceiptSlipItemVO> receiptSlipItemVOList = BeanMapUtils.mapListTo(receiptSlipItemRepo.findByReceiptSlipIdIn(receiptSlipIds), ReceiptSlipItemVO.class);

        return receiptSlipItemVOList.stream().collect(Collectors.toMap(ReceiptSlipItemVO::getReceiptSlipItemId, receiptSlipItemVO -> receiptSlipItemVO));
    }

    /**
     * @param receiptSlipInfos
     * @return
     */
    private List<StoringLineVO> createStoringLineInfos(Long storingPointId
                                                     , Map<Long, ReceiptSlipVO> receiptSlipMap
                                                     , Map<Long, ReceiptSlipItemVO> receiptSlipItemMap
                                                     , Map<Long, Long> storingListIdMap) {

        List<StoringLineVO> storingLineInfos = new ArrayList<StoringLineVO>();

        Long storingListId      = null;
        Long receiptSlipId      = null;
        ReceiptSlipVO receiptSlip;
        StoringLineVO storingLine;
        for (ReceiptSlipItemVO receiptSlipItem : receiptSlipItemMap.values()) {

            receiptSlipId = receiptSlipItem.getReceiptSlipId();
            storingListId = storingListIdMap.containsKey(receiptSlipId) ? storingListIdMap.get(receiptSlipId) : null;

            receiptSlip = receiptSlipMap.containsKey(receiptSlipId) ? receiptSlipMap.get(receiptSlipId) : null;

            storingLine = this.buildStoringLineVO(receiptSlip.getReceivedFacilityId()
                                                , storingListId
                                                , storingPointId
                                                , receiptSlip.getSlipNo()
                                                , receiptSlipItem);

            storingLineInfos.add(storingLine);
        }
        return storingLineInfos;
    }

    private List<StoringLineItemVO> createStoringLineItemInfosForSalesReturn(List<StoringLineVO> storingLineInfos
                                                                           , Map<Long, ReceiptSlipItemVO> receiptSlipItemMap) {

        List<StoringLineItemVO> storingLineItemInfos = new ArrayList<StoringLineItemVO>();

        Long receiptSlipItemId            = null;
        ReceiptSlipItemVO receiptSlipItem = null;
        for (StoringLineVO storingLine : storingLineInfos) {

            receiptSlipItemId = storingLine.getReceiptSlipItemId();
            receiptSlipItem = receiptSlipItemMap.containsKey(receiptSlipItemId) ? receiptSlipItemMap.get(receiptSlipItemId) : null;

            storingLineItemInfos.add(this.buildStoringLineItemVO(storingLine.getFacilityId()
                                                               , storingLine.getStoringLineId()
                                                               , receiptSlipItem));
        }

        return storingLineItemInfos;
    }


    private List<StoringLineItemVO> createStoringLineItemInfos(List<StoringLineVO> storingLineInfos
                                                             , Map<Long, ReceiptSlipItemVO> receiptSlipItemMap) {

        List<StoringLineItemVO> storingLineItemInfos    = new ArrayList<StoringLineItemVO>();

        Long receiptSlipItemId            = null;
        ReceiptSlipItemVO receiptSlipItem = null;
        List<StoringLineItemVO> storingLineItemSubInfos;
        for (StoringLineVO storingLine : storingLineInfos) {

            storingLineItemSubInfos = new ArrayList<StoringLineItemVO>();

            receiptSlipItemId = storingLine.getReceiptSlipItemId();
            receiptSlipItem   = receiptSlipItemMap.containsKey(receiptSlipItemId) ? receiptSlipItemMap.get(receiptSlipItemId)
                                                                                  : null;

            Map<LocationVO,BigDecimal> locationInfoWithQty = getLocationInStoringInstructionProcess(storingLine.getSiteId()
                                                                                                  , storingLine.getFacilityId()
                                                                                                  , storingLine.getProductId()
                                                                                                  , receiptSlipItem.getReceiptQty());

            for(LocationVO locationInfo:locationInfoWithQty.keySet()) {

                StoringLineItemVO storingLineItem = this.buildStoringLineItemVO(storingLine.getFacilityId()
                                                                              , storingLine.getStoringLineId()
                                                                              , receiptSlipItem);

                storingLineItem.setLocationId(locationInfo.getLocationId());
                storingLineItem.setLocationCd(locationInfo.getLocationCd());
                storingLineItem.setStoredQty(locationInfoWithQty.get(locationInfo));

                storingLineItemSubInfos.add(storingLineItem);
            }

            if (storingLineItemSubInfos.isEmpty()) {
                storingLineItemInfos.add(this.buildStoringLineItemVO(storingLine.getFacilityId()
                                                                   , storingLine.getStoringLineId()
                                                                   , receiptSlipItem));
            }else {
                storingLineItemInfos.addAll(storingLineItemSubInfos);
            }
        }

        return storingLineItemInfos;
    }

    /**
    *
    * doStoringReport mid1459 2024年5月17日
    * @param storingLineInfo
    */
    public void doStoringReport(StoringLineVO storingLineInfo) {

        if (storingLineInfo == null || storingLineInfo.getStoringLineId() == null) {
            return;
        }

        BigDecimal sumOfStoringLineItemStoredQty = BigDecimal.ZERO;
        BigDecimal sumOfStoringLineItemFrozenQty = BigDecimal.ZERO;
        List<StoringLineItemVO> storingLineItemInfos = BeanMapUtils.mapListTo(storingLineItemRepo.findByStoringLineId(storingLineInfo.getStoringLineId()), StoringLineItemVO.class);

        Set<Long> locationIds             = storingLineItemInfos.stream().map(StoringLineItemVO::getLocationId).collect(Collectors.toSet());
        List<LocationVO> locationVOList   = BeanMapUtils.mapListTo(locationRepo.findByLocationIdIn(locationIds), LocationVO.class);
        // Key : locationId Value : locationType
        Map<Long, String> locationTypeMap = locationVOList.stream().collect(Collectors.toMap(LocationVO::getLocationId, LocationVO::getLocationType, (c1, c2) -> c1));

        Long locationId     = null;
        String locationType = CommonConstants.CHAR_BLANK;
        for (StoringLineItemVO storingLineItem : storingLineItemInfos) {
            locationId   = storingLineItem.getLocationId();
            locationType = locationTypeMap.containsKey(locationId) ? locationTypeMap.get(locationId) : CommonConstants.CHAR_BLANK;

            if ( isStoredInNormalLocation(locationType) || isStoredInTentativeLocation(locationType) ) {
                sumOfStoringLineItemStoredQty = sumOfStoringLineItemStoredQty.add(storingLineItem.getStoredQty());
            }
            if ( isStoredInFrozenLocation(locationType) ) {
                sumOfStoringLineItemFrozenQty = sumOfStoringLineItemFrozenQty.add(storingLineItem.getStoredQty());
            }
        }
        storingLineInfo.setStoredQty(sumOfStoringLineItemStoredQty);
        storingLineInfo.setFrozenQty(sumOfStoringLineItemFrozenQty);
        storingLineInfo.setCompletedDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        storingLineInfo.setCompletedTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M_S));

        storingLineRepo.saveWithVersionCheck(BeanMapUtils.mapTo(storingLineInfo, StoringLine.class));

        Long storingListId = storingLineInfo.getStoringListId();
        if (isAllSotringLineCompletelyReported(storingLineInfo.getSiteId(),storingListId)) {

            StoringList storingListInfo = storingListRepo.findByStoringListId(storingListId);

            storingListInfo.setCompletedDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
            storingListInfo.setCompletedTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M_S));
            storingListRepo.saveWithVersionCheck(storingListInfo);
        }
    }

    private boolean isAllSotringLineCompletelyReported(String siteId
                                                     , Long storingListId) {
        // Assume all storing line are completely reported
        boolean result = true;

        List<StoringLineVO> storingLineVOInfos = BeanMapUtils.mapListTo(storingLineRepo.findByStoringListId(storingListId), StoringLineVO.class);

        if (storingLineVOInfos.isEmpty()) {
            return !result;
        }

        for (StoringLineVO storingLine : storingLineVOInfos) {
            BigDecimal insQty = storingLine.getInstuctionQty();
            BigDecimal storedQty = storingLine.getStoredQty();
            BigDecimal frozenQty = storingLine.getFrozenQty();

            if (insQty.compareTo(storedQty.add(frozenQty)) != 0) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     *
     * doStoringInstruction mid1459 2024年5月16日
     * @param storingPointId
     * @param receiptSlipInfos
     */
    public List<Long> doStoringInstruction(Long storingPointId
                                          ,String inventoryTransactionType
                                         , List<ReceiptSlipVO> receiptSlipInfos) {

        List<Long> resultList = new ArrayList<Long>();

        // 1 check arg
        chkArguments(storingPointId, receiptSlipInfos);

        // 2 create storingList list
        // Key : receiptSlipId Value : storingListId
        Map<Long, Long> storingListIdMap = new HashMap<>();
        List<StoringListVO> storingListInfos = createStoringList(storingPointId
                                                               , inventoryTransactionType
                                                               , receiptSlipInfos
                                                               , storingListIdMap
                                                               , resultList);

        // Key : receiptSlipId Value : receiptSlip
        Map<Long, ReceiptSlipVO> receiptSlipMap = receiptSlipInfos.stream().collect(Collectors.toMap(ReceiptSlipVO::getReceiptSlipId, receiptSlipVO -> receiptSlipVO));
        Map<Long, ReceiptSlipItemVO> receiptSlipItemMap = getReceiptSlipItemMap(receiptSlipMap);

        // 3 create storingLine list
        List<StoringLineVO> storingLineInfos = createStoringLineInfos(storingPointId
                                                                    , receiptSlipMap
                                                                    , receiptSlipItemMap
                                                                    , storingListIdMap);

        // 4 create storingLineItem list
        List<StoringLineItemVO> storingLineItemInfos = createStoringLineItemInfos(storingLineInfos
                                                                                , receiptSlipItemMap);
        // 3 commit storingList \ storingLine \ storingLineItem
        storingListRepo.saveInBatch(BeanMapUtils.mapListTo(storingListInfos,StoringList.class));
        storingLineRepo.saveInBatch(BeanMapUtils.mapListTo(storingLineInfos,StoringLine.class));
        storingLineItemRepo.saveInBatch(BeanMapUtils.mapListTo(storingLineItemInfos,StoringLineItem.class));

        return resultList;
    }

    private StoringListVO createStoringListByReceiptSlip(Long storingPointId
                                                       , String inventoryTransactionType
                                                       , ReceiptSlipVO receiptSlip) {

        String storingListNo = generateNoMgr.generateNonSerializedItemStoringListNo(receiptSlip.getSiteId()
                                                                                  , storingPointId);

        StoringListVO storingListVO = StoringListVO.create();

        storingListVO.setSiteId(receiptSlip.getSiteId());
        storingListVO.setFacilityId(receiptSlip.getReceivedFacilityId());
        storingListVO.setStoringPicId(receiptSlip.getReceivedPicId());
        storingListVO.setStoringPicNm(receiptSlip.getReceivedPicNm());
        storingListVO.setReceiptDate(receiptSlip.getReceivedDate());
        storingListVO.setStoringListNo(storingListNo);
        storingListVO.setInstructionDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        storingListVO.setInstructionTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M_S));
        storingListVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        storingListVO.setInventoryTransactionType(inventoryTransactionType);
        ;
        return storingListVO;
    }

    /**
     * 创建一个StoringLine
     */
    private StoringLineVO buildStoringLineVO(Long receivedFacilityId
                                           , Long storingListId
                                           , Long storingPointId
                                           , String receiptSlipNo
                                           , ReceiptSlipItemVO receiptSlipItem) {

        StoringLineVO storingLine = StoringLineVO.create();
        BigDecimal qty = receiptSlipItem.getReceiptQty().subtract(receiptSlipItem.getFrozenQty()); // receiptQty - frozenQty

        String storingListNo = generateNoMgr.generateNonSerializedItemStoringLineNo(receiptSlipItem.getSiteId(), storingPointId);

        storingLine.setSiteId(receiptSlipItem.getSiteId());
        storingLine.setFacilityId(receivedFacilityId);
        storingLine.setStoringLineNo(storingListNo);
        storingLine.setStoringListId(storingListId);
        storingLine.setReceiptSlipItemId(receiptSlipItem.getReceiptSlipItemId());
        storingLine.setCaseNo(receiptSlipItem.getCaseNo());
        storingLine.setProductId(receiptSlipItem.getProductId());
        storingLine.setProductCd(receiptSlipItem.getProductCd());
        storingLine.setProductNm(receiptSlipItem.getProductNm());
        storingLine.setOriginalInstQty(qty);
        storingLine.setInstuctionQty(qty);
        storingLine.setStoredQty(BigDecimal.ZERO);
        storingLine.setFrozenQty(BigDecimal.ZERO);
        storingLine.setReceiptSlipNo(receiptSlipNo);
        storingLine.setProductClassification(ProductClsType.PART.getCodeDbid());
        storingLine.setInventoryTransactionType(InventoryTransactionType.RETURNIN.getCodeDbid());

        return storingLine;
    }

    /**
     * 创建一个StoringLineItem
     */
    private StoringLineItemVO buildStoringLineItemVO(Long receivedFacilityId
                                                   , Long storingLineId
                                                   , ReceiptSlipItemVO receiptSlipItem) {

        StoringLineItemVO storingLineItemVO = new StoringLineItemVO();

        storingLineItemVO.setSiteId(receiptSlipItem.getSiteId());
        storingLineItemVO.setStoringLineId(storingLineId);
        storingLineItemVO.setFacilityId(receivedFacilityId);
        storingLineItemVO.setInstuctionQty(receiptSlipItem.getReceiptQty().subtract(receiptSlipItem.getFrozenQty()));
        storingLineItemVO.setStoredQty(receiptSlipItem.getReceiptQty());

        return storingLineItemVO;
    }

    private void chkArguments(Long storingPointId
                            , List<ReceiptSlipVO> receiptSlipInfos) {
        if (storingPointId == null) {
            throw new BusinessCodedException("Argument storingPointId must not be blank.");
        }
        if (receiptSlipInfos == null) {
            throw new BusinessCodedException("Argument receiptSlipInfos must not be null.");
        }
        if (receiptSlipInfos.isEmpty()) {
            throw new BusinessCodedException("Argument receiptSlipInfos must not be empty.");
        }
    }


    /**
     * Find the location in stock location for doStoringInstruction process
     * @param siteId
     * @param facilityId
     * @param productId
     * @author Lin ZhanWang
     * @return
     */
    private Map<LocationVO,BigDecimal>  getLocationInStoringInstructionProcess(String siteId
                                                                               , Long facilityId
                                                                               , Long productId
                                                                               , BigDecimal receiptQty) {

        LocationVO foundLocation = null;
        Map<LocationVO,BigDecimal> locationWithQty = new HashMap<LocationVO, BigDecimal>();

        //Get backOrder quantity
        BigDecimal boQty = this.getFacilityProductBoQty(siteId, facilityId, productId);
        boolean isNeedFindInTentative = boQty.signum()>0; // boQty>0, then to find the 'tentative' location
        boolean isTentativeFound = false;
        LocationVO tentativeLocation = null;
        LocationVO mainLocation = null;
        LocationVO normalLocation = null;
        List<ProductInventoryVO>  productInventoryList = this.findProductInventorys(productId, facilityId, siteId);

        Map<Long, LocationVO> locationMap = getLocationMap(productInventoryList);

        LocationVO location = null;
        Long locationId     = null;
        for (ProductInventoryVO proInv : productInventoryList) {

            locationId = proInv.getLocationId();

            location = locationMap.containsKey(locationId) ? locationMap.get(locationId) : null;

            String locationClassify = location.getLocationType();
            if (locationClassify.equals(LocationType.TENTATIVE.getCodeDbid()) ||locationClassify.equals(LocationType.NORMAL.getCodeDbid() )) {

                locationWithQty.put(location, new BigDecimal(0));

            } else {
                continue;
            }
            if(LocationType.TENTATIVE.getCodeDbid().equals(locationClassify)) {//If it's tentative.
                tentativeLocation = (tentativeLocation !=null) ? this.getMinLocation(tentativeLocation, location): location;
                isTentativeFound = true;
            }else {//Find the main location or normal locations(the smallest one);
                if(CommonConstants.CHAR_Y.equals(proInv.getPrimaryFlag())) {//If it's main location
                    mainLocation = location;
                }else {//Process the other normal locations.

                    normalLocation = (normalLocation !=null) ? this.getMinLocation(normalLocation, location): location;
                }
            }
        }
        if(isNeedFindInTentative) {//If it needs to find the tentative location but not found, it will get a random tentative location.
            if(!isTentativeFound) {
                LocationVO randomTentativeLocation = getTentativeLocationByFacility(siteId, facilityId); // Get a random tentative location.
                if(randomTentativeLocation ==null){//To find main, next to find normal in case of main not found
                    foundLocation = (mainLocation!=null) ? mainLocation : normalLocation;
                    locationWithQty.put(foundLocation, receiptQty);

                }else {
                    foundLocation = randomTentativeLocation;
                    if(boQty.compareTo(receiptQty)> -1) {

                        locationWithQty.put(foundLocation, receiptQty);
                    } else {

                        locationWithQty.put(foundLocation, boQty);
                        if(mainLocation!=null) {

                            locationWithQty.put(mainLocation, receiptQty.subtract(boQty));
                        } else if (normalLocation!= null) {
                            locationWithQty.put(normalLocation, receiptQty.subtract(boQty));
                        }
                    }
                }
            }else {
                foundLocation = tentativeLocation;
                if(boQty.compareTo(receiptQty)> -1) {

                    locationWithQty.put(foundLocation, receiptQty);
                } else {

                    locationWithQty.put(foundLocation, boQty);

                    locationWithQty.put(foundLocation, boQty);
                    if(mainLocation!=null) {

                        locationWithQty.put(mainLocation, receiptQty.subtract(boQty));
                    } else if (normalLocation!= null) {
                        locationWithQty.put(normalLocation, receiptQty.subtract(boQty));
                    }
                }
            }
        }else {//To find main, next to find normal in case of main not found
            if(mainLocation!=null) {

                locationWithQty.put(mainLocation, receiptQty.subtract(boQty));
            } else if (normalLocation!= null) {
                locationWithQty.put(normalLocation, receiptQty.subtract(boQty));
            }
        }
        return locationWithQty;
    }

    /**
     * @param productInventoryList
     * @return
     */
    private Map<Long, LocationVO> getLocationMap(List<ProductInventoryVO> productInventoryList) {

        Set<Long> locationIds = productInventoryList.stream().map(ProductInventoryVO::getLocationId).collect(Collectors.toSet());

        List<LocationVO> locationList = BeanMapUtils.mapListTo(locationRepo.findByLocationIdIn(locationIds), LocationVO.class);

        return locationList.stream().collect(Collectors.toMap(LocationVO::getLocationId, locationVO -> locationVO));
    }

    /**
     *
     * @param siteId
     * @param facilityId
     * @param productId
     * @return
     */
    private BigDecimal getFacilityProductBoQty(String siteId
                                             , Long facilityId
                                             , Long productId) {

        ProductStockStatus pss = this.inventoryMgr.doGetProductStockStatusInfo(siteId
                                                                              ,facilityId
                                                                              ,productId
                                                                              ,SpStockStatus.BO_QTY.getCodeDbid());
        return  (pss==null) ? BigDecimal.ZERO : pss.getQuantity();
    }

    /**
     *
     * @param productId
     * @param facilityId
     * @param siteId
     */
    private List<ProductInventoryVO> findProductInventorys(Long productId, Long facilityId, String siteId) {

        return BeanMapUtils.mapListTo(productInventoryRepo.findProductInventorys(siteId
                                                                               , facilityId
                                                                               , productId), ProductInventoryVO.class);
    }

    /**
     * Compare locationCd between two location entity, select the smaller one.
     * @param location1
     * @param location2
     * @return
     */
    private LocationVO getMinLocation(LocationVO location1, LocationVO location2) {
        String loc1 = (location1.getLocationCd() ==null) ? "" : location1.getLocationCd();
        String loc2 = (location2.getLocationCd() ==null) ? "" : location2.getLocationCd();
        return (loc1.compareTo(loc2) == 1) ? location2 : location1;
    }

    /**
     * Get the tentative location in the facility
     * @param siteId
     * @param facilityId
     * @return The found tentative location
     */
    private LocationVO getTentativeLocationByFacility(String siteId
                                                    , Long facilityId) {

        return BeanMapUtils.mapTo(locationRepo.getLocationVO(siteId
                                                           , facilityId
                                                           , LocationType.TENTATIVE.getCodeDbid()), LocationVO.class);
    }

    private boolean isStoredInTentativeLocation(String locationType) {
        return StringUtils.equals(PJConstants.LocationType.TENTATIVE.getCodeDbid(), locationType);
    }

    private boolean isStoredInFrozenLocation(String locationType) {
        return StringUtils.equals(PJConstants.LocationType.FROZEN.getCodeDbid(), locationType);
    }

    private boolean isStoredInNormalLocation(String locationType) {
        return StringUtils.equals(PJConstants.LocationType.NORMAL.getCodeDbid(), locationType);
    }
}