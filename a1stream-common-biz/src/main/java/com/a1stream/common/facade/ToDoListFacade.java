package com.a1stream.common.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.ToDoListBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.model.ToDoListForm;
import com.a1stream.common.service.ToDoListService;
import com.a1stream.domain.entity.MstCodeInfo;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.ServiceOrder;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;


/**
* 功能描述:
*
* @author mid1938
*/
@Component
public class ToDoListFacade {


    @Resource
    private ToDoListService toDoListService;

    public List<ToDoListBO> queryOrder(ToDoListForm model, String siteId) {

        List<ToDoListBO> toDoListBOs = new ArrayList<>();
        List<String> orderStatusList = new ArrayList<>();

        if(StringUtils.isNotEmpty(model.getOrderType())) {
            String orderStauts = CommonConstants.CHAR_BLANK;
            String zeroKmFlag = CommonConstants.CHAR_BLANK;

            //采购订单状态
            Map<String, String> purchaseOrderStatusMap = findStatusFromMstcode(MstCodeConstants.PurchaseOrderStatus.CODE_ID);
            //销售单状态
            Map<String, String> salesOrderStatusMap = findStatusFromMstcode(MstCodeConstants.SalesOrderStatus.CODE_ID);
            //服务单状态
            Map<String, String> serviceOrderStatusMap = findStatusFromMstcode(MstCodeConstants.ServiceOrderStatus.CODE_ID);

            if(StringUtils.equals(model.getOrderType(),PJConstants.OrderType.PURCHASEORDER.getCodeDbid())) {

                //指定特定的purchaseOrderStatus
                orderStatusList.add(MstCodeConstants.PurchaseOrderStatus.SPONPURCHASE);
                orderStatusList.add(MstCodeConstants.PurchaseOrderStatus.SPONRECEIVING);

                //查询对应的purchaseOrder
                List<PurchaseOrder> purchaseOrderToDoList = toDoListService.findPurchaseOrder(siteId, orderStatusList);

                //设置给返回的BO
                for (PurchaseOrder purchaseOrder : purchaseOrderToDoList) {

                    ToDoListBO toDoListBO = new ToDoListBO();
                    orderStauts = purchaseOrder.getOrderStatus();

                    toDoListBO.setOrderNo(purchaseOrder.getOrderNo());
                    toDoListBO.setOrderStatus(purchaseOrderStatusMap.containsKey(orderStauts)?purchaseOrderStatusMap.get(orderStauts):CommonConstants.CHAR_BLANK);
                    toDoListBO.setEmployeeName(purchaseOrder.getCreatedBy());
                    toDoListBO.setOrderDate(purchaseOrder.getOrderDate());
                    toDoListBO.setOrderId(purchaseOrder.getPurchaseOrderId());
                    toDoListBOs.add(toDoListBO);
                }

            }else if(StringUtils.equals(model.getOrderType(),PJConstants.OrderType.SALESORDER.getCodeDbid())) {

                //指定特定的salesOrder
                orderStatusList.add(MstCodeConstants.SalesOrderStatus.SP_WAITINGPICKING);
                orderStatusList.add(MstCodeConstants.SalesOrderStatus.SP_ONPICKING);

                //查询对应的salesOrder
                List<SalesOrder> salesOrdersToDoList = toDoListService.findSalesOrder(siteId, orderStatusList);

                //设置给返回的BO
                for (SalesOrder salseOrder : salesOrdersToDoList) {

                    ToDoListBO toDoListBO = new ToDoListBO();
                    orderStauts = salseOrder.getOrderStatus();

                    toDoListBO.setOrderNo(salseOrder.getOrderNo());
                    toDoListBO.setOrderStatus(salesOrderStatusMap.containsKey(orderStauts)?salesOrderStatusMap.get(orderStauts):CommonConstants.CHAR_BLANK);
                    toDoListBO.setEmployeeName(salseOrder.getCreatedBy());
                    toDoListBO.setOrderDate(salseOrder.getOrderDate());
                    toDoListBO.setOrderId(salseOrder.getSalesOrderId());
                    toDoListBOs.add(toDoListBO);
                }

            }else if(StringUtils.equals(model.getOrderType(),PJConstants.OrderType.SERVICEORDER.getCodeDbid())) {

                zeroKmFlag = CommonConstants.CHAR_N;
              //查询对应的serviceOrder
                extracted(siteId, toDoListBOs, orderStatusList, serviceOrderStatusMap,zeroKmFlag);

            }else if(StringUtils.equals(model.getOrderType(),PJConstants.OrderType.SERVICEFOR0KMMC.getCodeDbid())) {

                zeroKmFlag = CommonConstants.CHAR_Y;
              //查询对应的serviceOrder
                extracted(siteId, toDoListBOs, orderStatusList, serviceOrderStatusMap,zeroKmFlag);

            }else if(StringUtils.equals(model.getOrderType(),PJConstants.OrderType.BATTERYCLAIM.getCodeDbid())) {
                //指定特定的batteryClaim
                orderStatusList.add(MstCodeConstants.ServiceOrderStatus.WAIT_FOR_SETTLE);
                //查询对应的batteryClaimOrder
                List<ServiceOrder> batteryClaimOrders = toDoListService.findBatteryClaim(siteId
                                                                                        ,CommonConstants.CHAR_N
                                                                                        ,orderStatusList
                                                                                        ,CommonConstants.CHAR_Y
                                                                                        ,PJConstants.ServiceCategory.CLAIMBATTERY.getCodeDbid());

              //设置给返回的BO
                for (ServiceOrder serviceOrder : batteryClaimOrders) {

                    ToDoListBO toDoListBO = new ToDoListBO();
                    orderStauts = serviceOrder.getOrderStatusId();

                    toDoListBO.setOrderNo(serviceOrder.getOrderNo());
                    toDoListBO.setOrderStatus(serviceOrderStatusMap.containsKey(orderStauts)?serviceOrderStatusMap.get(orderStauts):CommonConstants.CHAR_BLANK);
                    toDoListBO.setEmployeeName(serviceOrder.getCreatedBy());
                    toDoListBO.setOrderDate(serviceOrder.getOrderDate());
                    toDoListBO.setBrand(serviceOrder.getBrandContent());
                    toDoListBO.setOrderId(serviceOrder.getServiceOrderId());
                    toDoListBOs.add(toDoListBO);
                }
            }
        }
        Collections.sort(toDoListBOs, Comparator.comparing(ToDoListBO::getOrderNo));
        return toDoListBOs;
    }

    /**
     * @param siteId
     * @param toDoListBOs
     * @param orderStatusList
     * @param serviceOrderStatusMap
     */
    private void extracted(String siteId, List<ToDoListBO> toDoListBOs, List<String> orderStatusList, Map<String, String> serviceOrderStatusMap,String zeroKmFlag) {

        String orderStauts;
        //指定特定的serviceOrder
            orderStatusList.add(MstCodeConstants.ServiceOrderStatus.WAIT_FOR_SETTLE);

          //查询对应的serviceOrder
            List<ServiceOrder> serviceOrderToDoList = toDoListService.findServiceOrder(siteId, zeroKmFlag,orderStatusList);

            //设置给返回的BO
            for (ServiceOrder serviceOrder : serviceOrderToDoList) {

                ToDoListBO toDoListBO = new ToDoListBO();
                orderStauts = serviceOrder.getOrderStatusId();

                toDoListBO.setOrderNo(serviceOrder.getOrderNo());
                toDoListBO.setOrderStatus(serviceOrderStatusMap.containsKey(orderStauts)?serviceOrderStatusMap.get(orderStauts):CommonConstants.CHAR_BLANK);
                toDoListBO.setEmployeeName(serviceOrder.getCreatedBy());
                toDoListBO.setOrderDate(serviceOrder.getOrderDate());
                toDoListBO.setBrand(serviceOrder.getBrandContent());
                toDoListBO.setOrderId(serviceOrder.getServiceOrderId());
                toDoListBOs.add(toDoListBO);
            }
    }

    private Map<String, String> findStatusFromMstcode(String codeId){

        List<MstCodeInfo> orderStatusList = toDoListService.findCodeId(codeId);

        return orderStatusList.stream().collect(Collectors.toMap(MstCodeInfo::getCodeDbid, MstCodeInfo::getCodeData1));
    }
}