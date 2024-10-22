package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.parts.DIM020401BO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.parts.service.DIM0204Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Unfinished Order Cancel
*
* @author mid2178
*/
@Component
public class DIM0204Facade {

    @Resource
    private DIM0204Service dim0204Service;

    public List<DIM020401BO> doSparePartsDownload(Long facilityId, String siteId) {

        return getSparePartsDownloadInfo(facilityId, siteId);
    }

    public List<DIM020401BO> doServiceDownload(Long facilityId, String siteId) {

        return getServiceDownloadInfo(facilityId, siteId);
    }

    public List<DIM020401BO> doPartsStoringDownload(Long facilityId, String siteId) {

        return getPartsStoringDownloadInfo(facilityId, siteId);
    }

    public void doSparePartsCancel(Long facilityId, String siteId) {

        // 校验:如果没在盘点中，提示信息
        doValidate(facilityId, siteId);

        List<DIM020401BO> sparePartsInfo = getSparePartsDownloadInfo(facilityId, siteId);
        Set<Long> salesOrderIds = sparePartsInfo.stream().map(DIM020401BO::getSalesOrderId).collect(Collectors.toSet());

        // 更新SalesOrderItem表
        List<SalesOrderItemVO> itemVOs = dim0204Service.getCancelSalesOrderItems(salesOrderIds, siteId);

        for(SalesOrderItemVO itemVO: itemVOs) {

            itemVO.setWaitingAllocateQty(BigDecimal.ZERO);
            itemVO.setAllocatedQty(BigDecimal.ZERO);
            itemVO.setBoQty(BigDecimal.ZERO);
        }

        dim0204Service.saveSparePartsCancel(itemVOs);
    }

    public void doServiceCancel(Long facilityId, String facilityCd, String siteId) {

        // 校验:如果没在盘点中，提示信息
        doValidate(facilityId, siteId);

        // 取消召回，更新车台，取消销售单，取消服务单
        updateService(facilityId, facilityCd, siteId);

    }

    private void updateService(Long facilityId, String facilityCd, String siteId) {

        // 获取需要对象 salesOrderIds
        List<DIM020401BO> serviceInfo = getServiceDownloadInfo(facilityId, siteId);
        if(serviceInfo.isEmpty()) {
            return;
        }

        // 获取需要Cancel对象 cancelServiceOrderList
        Set<Long> salesOrderIds = serviceInfo.stream().map(DIM020401BO::getSalesOrderId).collect(Collectors.toSet());
        List<ServiceOrderVO> serviceOrderList = dim0204Service.getCancelSVList(salesOrderIds, siteId, facilityId);

        // 1.取消服务单
        serviceOrderList.forEach(vo -> vo.setOrderStatusId(MstCodeConstants.ServiceOrderStatus.CANCELLED));

        // 2.取消销售单
        List<SalesOrderVO> salesOrderList = cancelSalesOrder(salesOrderIds);

        // 3.如果有召回内容，做召回取消处理
        List<CmmSpecialClaimSerialProVO> claimSerialProList = cancelClaim(facilityCd, siteId, serviceOrderList);

        // 4.更新车台(LOCAL\CMM 两张表)品质状态为正常
        Set<Long> cmmSerialProIds = serviceOrderList.stream().map(ServiceOrderVO::getCmmSerializedProductId).collect(Collectors.toSet());

        List<SerializedProductVO> serialProVOs= dim0204Service.getSerialPro(cmmSerialProIds);
        serialProVOs.forEach(vo -> vo.setQualityStatus(PJConstants.SerialProQualityStatus.NORMAL));

        List<CmmSerializedProductVO> cmmSerialProVOs= dim0204Service.getcmmSerialPro(cmmSerialProIds);
        cmmSerialProVOs.forEach(vo -> vo.setQualityStatus(PJConstants.SerialProQualityStatus.NORMAL));

        // 取消服务单，取消销售单，取消召回，更新车台
        dim0204Service.doServiceCancel(serviceOrderList, salesOrderList, claimSerialProList, serialProVOs, cmmSerialProVOs);
    }

    public void doPartsStoringCancel(Long facilityId, String siteId) {

        // 校验:如果没在盘点中，提示信息
        doValidate(facilityId, siteId);

        List<DIM020401BO> partsStoringList = getPartsStoringDownloadInfo(facilityId, siteId);
        Set<Long> storingLineIds = partsStoringList.stream().map(DIM020401BO::getStoringLineId).collect(Collectors.toSet());

        // 更新storingLine的StoredQty
        List<StoringLineVO> storingLineList = dim0204Service.findByStoringLineIdIn(storingLineIds);
        for(StoringLineVO sLineVO : storingLineList) {

            sLineVO.setStoredQty(sLineVO.getInstuctionQty());
        }

       // 更新storingLineItem的StoredQty
        List<StoringLineItemVO> storingLineItemList = dim0204Service.getByStoringLineIdIn(siteId, storingLineIds);
        for(StoringLineItemVO sLineItemVO : storingLineItemList) {

            sLineItemVO.setStoredQty(sLineItemVO.getInstuctionQty());
        }

        dim0204Service.doPartsStoringCancel(storingLineList, storingLineItemList);
    }

    public void doLocationDelete(Long facilityId, String siteId) {

        // 校验:如果没在盘点中，提示信息
        doValidate(facilityId, siteId);

        // 删除库存状态数据
        List<ProductStockStatusVO> proStocStatusDel = dim0204Service.getProStocStatus(siteId, facilityId);

        // 库存库位数据
        List<ProductInventoryVO> proInvDel = dim0204Service.getProInventory(siteId, facilityId);

        // 删除库位基础数据
        List<LocationVO> locationDel = dim0204Service.getLocation(siteId, facilityId);

        dim0204Service.doLocationDelete(proStocStatusDel, proInvDel, locationDel);
    }

    private List<CmmSpecialClaimSerialProVO> cancelClaim(String facilityCd, String siteId, List<ServiceOrderVO> cancelServiceOrderList) {

        List<CmmSpecialClaimSerialProVO> claimSerialProList = new ArrayList<>();

        // 获取满足相关条件的已完成召回对象：CmmSpecialClaimSerialProList
        Set<Long> claimIds = cancelServiceOrderList.stream().map(ServiceOrderVO::getCmmSpecialClaimId).collect(Collectors.toSet());
        Set<String> frameNos = cancelServiceOrderList.stream().map(ServiceOrderVO::getFrameNo).collect(Collectors.toSet());
        List<CmmSpecialClaimSerialProVO> claimVOs = dim0204Service.getByClaimIds(siteId ,facilityCd ,CommonConstants.CHAR_Y ,frameNos ,claimIds);

        // claimVOMap <frameNo+'|'+SpecialClaimId , ProductInventoryVO>
        Map<String, CmmSpecialClaimSerialProVO> claimVOMap = claimVOs.stream().collect(Collectors.toMap(
                                                             v -> v.getFrameNo() + CommonConstants.CHAR_VERTICAL_BAR + v.getSpecialClaimId().toString(),
                                                             Function.identity()));

        for(ServiceOrderVO serOrderVO : cancelServiceOrderList) {

            String key = serOrderVO.getFrameNo() + CommonConstants.CHAR_VERTICAL_BAR + serOrderVO.getCmmSpecialClaimId().toString();
            if(serOrderVO.getCmmSpecialClaimId() != null && claimVOMap.containsKey(key)) {// 已完成召回

                // 取消召回
                claimVOMap.get(key).setClaimFlag(CommonConstants.CHAR_N);
                claimSerialProList.add(claimVOMap.get(key));
            }
        }

        return claimSerialProList;
    }

    private List<SalesOrderVO> cancelSalesOrder(Set<Long> salesOrderIds) {

        List<SalesOrderVO> cancelSalesOrderList = dim0204Service.findBySalesOrderIdIn(salesOrderIds);
        cancelSalesOrderList.forEach(vo -> vo.setOrderStatus(MstCodeConstants.SalesOrderStatus.CANCELLED));

        return cancelSalesOrderList;
    }

    private List<DIM020401BO> getSparePartsDownloadInfo(Long facilityId, String siteId) {

        return dim0204Service.getSparePartsDownloadInfo(facilityId, siteId);
    }

    private List<DIM020401BO> getServiceDownloadInfo(Long facilityId, String siteId) {

        return dim0204Service.getServiceDownloadInfo(facilityId, siteId);
    }

    private List<DIM020401BO> getPartsStoringDownloadInfo(Long facilityId, String siteId) {

        return dim0204Service.getPartsStoringDownloadInfo(facilityId, siteId);
    }

    public void doValidate(Long facilityId, String siteId) {

        SystemParameterVO sysParam = dim0204Service.getProcessingSystemParameter(siteId, facilityId, MstCodeConstants.SystemParameterType.STOCKTAKING, CommonConstants.CHAR_ONE);
        if(sysParam == null) {// 如果没在盘点中，提示信息

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }
    }
}
