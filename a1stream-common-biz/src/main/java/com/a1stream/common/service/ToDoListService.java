package com.a1stream.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.entity.MstCodeInfo;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;

import jakarta.annotation.Resource;

@Service
public class ToDoListService {

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private ServiceOrderRepository serviceOrderRepository;

    @Resource
    private MstCodeInfoRepository mstCodeInfoRepository;

    /**
     * @param siteId
     * @param orderStatusList
     * @return
     */
    public List<PurchaseOrder> findPurchaseOrder(String siteId, List<String> orderStatusList) {
        return purchaseOrderRepository.findBySiteIdAndOrderStatusIn(siteId, orderStatusList);
    }

    /**
     * @param siteId
     * @param orderStatusList
     * @return
     */
    public List<SalesOrder> findSalesOrder(String siteId, List<String> orderStatusList) {
        return salesOrderRepository.findBySiteIdAndOrderStatusIn(siteId, orderStatusList);
    }

    /**
     * @param siteId
     * @param charN
     * @param orderStatusList
     * @param charY
     * @param codeDbid
     * @return
     */
    public List<ServiceOrder> findBatteryClaim(String siteId, String charN, List<String> orderStatusList, String charY, String codeDbid) {
        return serviceOrderRepository.findBySiteIdAndZeroKmFlagAndOrderStatusIdInAndEvFlagAndServiceCategoryId(siteId
                                                                                                            ,CommonConstants.CHAR_N
                                                                                                            ,orderStatusList
                                                                                                            ,CommonConstants.CHAR_Y
                                                                                                            ,PJConstants.ServiceCategory.CLAIMBATTERY.getCodeDbid());
    }

    /**
     * @param siteId
     * @param zeroKmFlag
     * @param orderStatusList
     * @return
     */
    public List<ServiceOrder> findServiceOrder(String siteId, String zeroKmFlag, List<String> orderStatusList) {
        return serviceOrderRepository.findBySiteIdAndZeroKmFlagAndOrderStatusIdIn(siteId, zeroKmFlag,orderStatusList);
    }

    /**
     * @param codeId
     * @return
     */
    public List<MstCodeInfo> findCodeId(String codeId) {
        return mstCodeInfoRepository.findByCodeId(codeId);
    }
}
