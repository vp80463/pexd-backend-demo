package com.a1stream.common.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.PickingResultItemBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.utils.ListSortUtil;
import com.a1stream.domain.entity.PickingItem;
import com.a1stream.domain.entity.PickingList;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.PickingItemRepository;
import com.a1stream.domain.repository.PickingListRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PickingItemVO;
import com.a1stream.domain.vo.PickingListVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

@Component
public class PickingInstructionManager {

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepo;

    @Resource
    private PickingItemRepository pickingItemRepo;

    @Resource
    private PickingListRepository pickingListRepo;

    @Resource
    private LocationRepository locationRepo;

    @Resource
    private MstProductRepository productRepo;

    @Resource
    private InventoryManager inventoryMgr;

    @Resource
    private GenerateNoManager generateNoMgr;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    private final static int DEFAULT_SIZE_IF_DIVIDE_MAXDU_0 = 20;

    public List<PickingListVO> createPickingListInfos(List<DeliveryOrderVO> deliveryOrders
                                                    , int maxDU
                                                    , int maxLine
                                                    , String batchNo
                                                    , String siteId) {

        int realMaxDU = (maxDU==0) ? DEFAULT_SIZE_IF_DIVIDE_MAXDU_0 : maxDU;

        return getPickingListResultList(deliveryOrders, realMaxDU, maxLine, siteId);
    }

    private List<PickingListVO> getPickingListResultList(List<DeliveryOrderVO> deliveryOrders, int maxDU, int maxLines, String siteId) {

        if (deliveryOrders.isEmpty()) {
            return null;
        }

        List<PickingListVO> pickingLists = new ArrayList<>();
        List<PickingItemVO> pickingItemList = new ArrayList<>();

        DeliveryOrderVO dor = deliveryOrders.get(0);
        Long fromFacilityId = dor.getFromFacilityId();
        String fromFacilityCd = mstFacilityRepository.findByFacilityId(fromFacilityId).getFacilityCd();
        Long fromOrgId = dor.getFromOrganizationId();

        boolean isByDu = (maxDU > 0); boolean isByLine = (maxLines > 0);
        int orderIdx = 0; int orderCount = deliveryOrders.size();

        Set<Long> deliveryOrderIds = deliveryOrders.stream().map(DeliveryOrderVO::getDeliveryOrderId).collect(Collectors.toSet());
        List<DeliveryOrderItemVO> deliveryOrderItemList = BeanMapUtils.mapListTo(deliveryOrderItemRepo.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderItemVO.class);
        Map<Long, List<DeliveryOrderItemVO>> deliveryOrderItemGroup = deliveryOrderItemList.stream().collect(Collectors.groupingBy(v -> v.getDeliveryOrderId() ));

        List<LocationVO> locationList = BeanMapUtils.mapListTo(locationRepo.findBySiteId(siteId), LocationVO.class);
        Map<Long, LocationVO> locationMap = locationList.stream().collect(Collectors.toMap(LocationVO::getLocationId, Function.identity()));

        Set<Long> productIds = deliveryOrderItemList.stream().map(DeliveryOrderItemVO::getProductId).collect(Collectors.toSet());
        List<MstProductVO> productVOList = BeanMapUtils.mapListTo(productRepo.findByProductIdIn(productIds), MstProductVO.class);
        Map<Long, MstProductVO> productInfoMap = productVOList.stream().collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M);

        List<PickingResultItemBO> items = new ArrayList<>();
        for (DeliveryOrderVO deliveryOrder : deliveryOrders) {
            List<DeliveryOrderItemVO> orderItemList = deliveryOrderItemGroup.get(deliveryOrder.getDeliveryOrderId());
            for (DeliveryOrderItemVO orderItem : orderItemList) {
                List<PickingResultItemBO> tempItems = inventoryMgr.doPickingInstruction(siteId, orderItem.getProductId(), fromFacilityId, orderItem.getDeliveryQty(), locationMap, fromFacilityCd, orderItem.getProductCd());
                this.bindResultToDeliveryOrderItem(tempItems, orderItem);

                items.addAll(tempItems);
            }
            orderIdx++;

            int itemSize = items.size();
            if (orderIdx == orderCount
                || (!isByDu && (isByLine && itemSize >= maxLines))
                || (isByDu && orderIdx % maxDU == 0)
                || (isByLine && itemSize >= maxLines)) {

                Map<Long, List<PickingResultItemBO>> wzMap = groupByWorkZoneId(items);
                for (List<PickingResultItemBO> pkItemsList : wzMap.values()) {
                    this.registerOnePickingList(pkItemsList, fromOrgId, fromFacilityId, siteId, sysDate, sysTime, productInfoMap, pickingLists, pickingItemList);
                }
                items.clear();
            }
        }

        // save db
        pickingListRepo.saveInBatch(BeanMapUtils.mapListTo(pickingLists, PickingList.class));
        pickingItemRepo.saveInBatch(BeanMapUtils.mapListTo(pickingItemList, PickingItem.class));

        return pickingLists;
    }

    public List<Long> doPickingCompletion(List<Long> deliveryOrderIdToReports) {

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M);

        List<PickingItemVO> pickingItemList = BeanMapUtils.mapListTo(pickingItemRepo.findByDeliveryOrderIdIn(deliveryOrderIdToReports), PickingItemVO.class);
        Set<Long> pickingListIds = pickingItemList.stream().map(PickingItemVO::getPickingListId).collect(Collectors.toSet());

        List<PickingListVO> pickingList = BeanMapUtils.mapListTo(pickingListRepo.findByPickingListIdIn(pickingListIds), PickingListVO.class);

        // 更新完成时间
        for (PickingItemVO item : pickingItemList) {
            item.setFinishedDate(sysDate);
            item.setFinishedTime(sysTime);
        }

        for (PickingListVO item : pickingList) {
            item.setFinishedDate(sysDate);
            item.setFinishedTime(sysTime);
        }

        pickingItemRepo.saveInBatch(BeanMapUtils.mapListTo(pickingItemList, PickingItem.class));
        pickingListRepo.saveInBatch(BeanMapUtils.mapListTo(pickingList, PickingList.class));

        return deliveryOrderIdToReports;
    }

    private List<PickingResultItemBO> bindResultToDeliveryOrderItem(List<PickingResultItemBO> items, DeliveryOrderItemVO orderItem) {

        for (PickingResultItemBO pickingResultItem : items) {
            pickingResultItem.setBindEntity(orderItem);
        }
        return items;
    }

    private Map<Long, List<PickingResultItemBO>> groupByWorkZoneId(List<PickingResultItemBO> pkitems) {

        Map<Long, List<PickingResultItemBO>> wzMap = new HashMap<>();
        List<PickingResultItemBO> pkitemListGroupByWzId;

        for (PickingResultItemBO pickingResultItem : pkitems) {
            Long key = pickingResultItem.getPickingLocation().getWorkzoneId();
            if (wzMap.get(key) == null) {
                pkitemListGroupByWzId = new ArrayList<>();
                pkitemListGroupByWzId.add(pickingResultItem);
            } else {
                pkitemListGroupByWzId = wzMap.get(key);
                pkitemListGroupByWzId.add(pickingResultItem);
            }
            wzMap.put(key, pkitemListGroupByWzId);
        }

        return wzMap;
    }

    private void registerOnePickingList(List<PickingResultItemBO> pkitems
                                            , Long fromOrgId
                                            , Long fromFacilityId
                                            , String siteId
                                            , String sysDate
                                            , String sysTime
                                            , Map<Long, MstProductVO> productInfoMap
                                            , List<PickingListVO> pickingLists
                                            , List<PickingItemVO> pickingItemList) {

        //Create a builder
        PickingListVO pickingListVO = buildPickingListVO(fromFacilityId, siteId, sysDate, sysTime);
        //Sort by picking location id field.
        ListSortUtil.sort(pkitems, new String[] {"pickingLocation.locationId"});
        //Create and Put the issuance item to pickingListBuidler
        int seq = 1;
        for (PickingResultItemBO item : pkitems) {

            PickingItemVO pickingItemVO = buildPickingItemVO(siteId, sysDate, sysTime, productInfoMap, pickingListVO, seq, item);
            seq++;
            pickingItemList.add(pickingItemVO);
        }

        pickingLists.add(pickingListVO);
    }

    private PickingItemVO buildPickingItemVO(String siteId, String sysDate, String sysTime, Map<Long, MstProductVO> productInfoMap, PickingListVO pickingListVO, int seq, PickingResultItemBO item) {

        PickingItemVO pickingItemVO = new PickingItemVO();

        pickingItemVO.setSiteId(siteId);
        pickingItemVO.setPickingListId(pickingListVO.getPickingListId());
        pickingItemVO.setPickingListNo(pickingListVO.getPickingListNo());
        pickingItemVO.setSeqNo(seq+"");
        pickingItemVO.setStartedDate(sysDate);
        pickingItemVO.setStartedTime(sysTime);
        pickingItemVO.setProductId(item.getPartsId());
        MstProductVO productVO = productInfoMap.containsKey(item.getPartsId())? productInfoMap.get(item.getPartsId()) : new MstProductVO();
        pickingItemVO.setProductCd(productVO.getProductCd());
        pickingItemVO.setProductNm(productVO.getSalesDescription());
        pickingItemVO.setQty(item.getPickingQuantity());
        LocationVO locationVO = item.getPickingLocation();
        pickingItemVO.setLocationId(locationVO.getLocationId());
        pickingItemVO.setLocationCd(locationVO.getLocationCd());
        pickingItemVO.setDeliveryOrderId(item.getBindEntity().getDeliveryOrderId());
        pickingItemVO.setDeliveryOrderItemId(item.getBindEntity().getDeliveryOrderItemId());
        pickingItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        pickingItemVO.setFacilityId(pickingListVO.getFacilityId());

        return pickingItemVO;
    }

    private PickingListVO buildPickingListVO(Long fromFacilityId, String siteId, String sysDate, String sysTime) {

        PickingListVO pickingListVO = PickingListVO.create();

        pickingListVO.setSiteId(siteId);
        pickingListVO.setFacilityId(fromFacilityId);
        pickingListVO.setPickingListNo(generateNoMgr.generatePickingListNo(siteId, fromFacilityId));
        pickingListVO.setPickingListDate(sysDate);
        pickingListVO.setStartedDate(sysDate);
        pickingListVO.setStartedTime(sysTime);
        pickingListVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        pickingListVO.setUpdateCount(0);
        pickingListVO.setInventoryTransactionType(InventoryTransactionType.SALESTOCKOUT.getCodeDbid());

        return pickingListVO;
    }
}