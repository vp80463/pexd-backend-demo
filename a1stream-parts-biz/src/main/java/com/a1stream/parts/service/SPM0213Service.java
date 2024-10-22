/**
 *
 */
package com.a1stream.parts.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.domain.bo.parts.SPM021301BO;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.form.parts.SPM021301Form;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPM0213Service {

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private CallNewIfsManager callNewIfsManager;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private PartsSalesStockAllocationManager partsSalesStockAllocationManager;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public SystemParameterVO getSystemParameter(String paramCode) {

        return BeanMapUtils.mapTo(systemParameterRepository.findBySystemParameterTypeId(paramCode), SystemParameterVO.class);
    }

    public List<ProductStockStatusVO> findStockStatusList( String siteId
                                                        ,  Long targetReceiptPointId
                                                        ,  Set<Long> productIds
                                                        ,  String productClassification
                                                        ,  Set<String> productStockStatusTypes){

        return  BeanMapUtils.mapListTo(productStockStatusRepository.findStockStatusList(siteId, targetReceiptPointId, productIds, productClassification, productStockStatusTypes), ProductStockStatusVO.class);
    }

    public Page<SPM021301BO> searchBackOrderList(SPM021301Form model) {

        return salesOrderRepository.searchBackOrderList(model);
    }

    public SalesOrderVO findSalesOrderByOrderId(Long orderId) {

        return BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(orderId), SalesOrderVO.class);
    }

    public List<SalesOrderVO> findBySalesOrderIdIn(Set<Long> orderId){

        return BeanMapUtils.mapListTo(salesOrderRepository.findBySalesOrderIdIn(orderId), SalesOrderVO.class);
    }

    public List<SalesOrderItemVO> findSalesOrderItem(Long orderId,String siteId){

        return BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderIdAndSiteId(orderId,siteId), SalesOrderItemVO.class);
    }

    public List<SalesOrderItemVO> findBySalesOrderIds(Set<Long> orderId,String siteId){

        return BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderIds(siteId,orderId), SalesOrderItemVO.class);
    }

    public void saveSalesOrder(SalesOrderVO model) {
        salesOrderRepository.save(BeanMapUtils.mapTo(model, SalesOrder.class));
    }

    public void executeStockAllocation(SalesOrderVO salesOrderInfo,List<SalesOrderItemVO> orderItemVoList){

        partsSalesStockAllocationManager.executeStockAllocation(salesOrderInfo, orderItemVoList);
    }

    public String searchPurchaseStatus(String[][] ps) {

        LinkedHashMap<String, Object> map = Arrays.stream(ps).collect(LinkedHashMap::new, (m, v) -> m.put(v[0], v[1]), HashMap::putAll);
        String ifsCode = InterfCode.DMSTOSP_POPROGRESS_INQ;
        BaseInfResponse callNewIfsService = callNewIfsManager.callGetMethodNewIfsService(ifsRequestUrl, ifsCode, map);
        return callNewIfsService.getData();
    }

}
