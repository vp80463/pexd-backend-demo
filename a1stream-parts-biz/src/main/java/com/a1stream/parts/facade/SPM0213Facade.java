/**
 *
 */
package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.domain.bo.parts.SPM021301BO;
import com.a1stream.domain.bo.parts.SPM021302BO;
import com.a1stream.domain.bo.parts.SPM021303BO;
import com.a1stream.domain.form.parts.SPM021301Form;
import com.a1stream.domain.form.parts.SPM021302Form;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.parts.service.SPM0213Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPM0213Facade {

    private static final String CHAR_STATUS                           = "status";
    private static final String CHAR_BO_QTY                           = "boQty";
    private static final String CHAR_ALLOCATED_QTY                    = "allocatedQty";
    private static final String CHAR_INVOICE_QTY                      = "invoicedQty";
    private static final String CHAR_ONSHIPPING_QTY                   = "onShippingQty";
    private static final String CHAR_CANCELLED_QTY                    = "cancelledQty";
    private static final String CHAR_ATP_STATUS                       = "atpSts";
    private static final String CHAR_TARGET_ARRIVAL_DATE              = "targetArrivalDate";
    private static final String CHAR_TARGET_DELIVERY_DATE             = "targetDeliveryDate";
    private static final String ARG_ONE = "requestPassword";
    private static final String ARG_TWO = "requestTime";
    private static final String ARG_THREE = "poNo";
    private static final String ARG_FOUR = "dealerCode";
    private static final String ARG_FIVE = "requestId";
    private static final String ARG_SIX = "partsNo";
    private static final String ARG_SEVEN = "sysOwnerCd";

    @Resource
    private SPM0213Service spm0213Service;

    @Resource
    private ConstantsLogic constantsLogic;

    public Page<SPM021301BO> searchBackOrderList(SPM021301Form model) {

        Page<SPM021301BO> result =spm0213Service.searchBackOrderList(model);

        Set<Long> idSet = result.stream()
                                .map(SPM021301BO::getOrderId)
                                .collect(Collectors.toSet());

        List<SalesOrderItemVO> items= spm0213Service.findBySalesOrderIds(idSet, model.getSiteId());

        //抽取productId
        Set<Long> productIdSet = items.stream()
                                      .map(SalesOrderItemVO::getProductId)
                                      .collect(Collectors.toSet());

        Set<String> productStockStatusTypes = new HashSet<>();
        productStockStatusTypes.add(PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
        List<ProductStockStatusVO> pStatusVOs = spm0213Service.findStockStatusList(model.getSiteId(), model.getPointId(), productIdSet, PJConstants.ProductClsType.PART.getCodeDbid(), productStockStatusTypes);

        Map<Long, List<SalesOrderItemVO>> salesOrderItemMaps = items.stream()
                .collect(Collectors.groupingBy(SalesOrderItemVO::getSalesOrderId));

        Map<Long, ProductStockStatusVO> productMaps = pStatusVOs.stream()
                .collect(Collectors.toMap(ProductStockStatusVO::getProductId, obj -> obj));

        for (SPM021301BO spm021301bo : result) {

            List<SalesOrderItemVO> itemVOs = salesOrderItemMaps.get(spm021301bo.getOrderId());
            if(!ObjectUtils.isEmpty(itemVOs)) {
                for (SalesOrderItemVO item : itemVOs) {
                    if (!ObjectUtils.isEmpty(item) && !ObjectUtils.isEmpty(item.getBoQty())
                       && item.getBoQty().compareTo(BigDecimal.ZERO) == CommonConstants.INTEGER_ONE) {

                        ProductStockStatusVO productStockStatusVO = productMaps.get(item.getProductId());
                        if(!ObjectUtils.isEmpty(productStockStatusVO) && productStockStatusVO.getQuantity().compareTo(BigDecimal.ZERO) == CommonConstants.INTEGER_ONE) {

                            spm021301bo.setBoRelease(CommonConstants.CHAR_Y);
                        }
                   }
                }
            }
        }
        return result;
    }

    public void editBackOrder(SPM021302Form model) {

        SalesOrderVO salesOrderVO = spm0213Service.findSalesOrderByOrderId(model.getOrderId());

        salesOrderVO.setDeliveryPlanDate(model.getCustomerPromiseDate());
        salesOrderVO.setBoContactContentType(model.getContactContent());
        salesOrderVO.setComment(model.getComment());
        if(StringUtils.isNotBlank(model.getContactDate())) {
            salesOrderVO.setBoContactDate(model.getContactDate().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_EIGHT));
            salesOrderVO.setBoContactTime(model.getContactDate().substring(CommonConstants.INTEGER_EIGHT, CommonConstants.INTEGER_TWELVE));
        }else {
            salesOrderVO.setBoContactDate(CommonConstants.CHAR_BLANK);
            salesOrderVO.setBoContactTime(CommonConstants.CHAR_BLANK);
        }
        spm0213Service.saveSalesOrder(salesOrderVO);

    }

    public void boRelease(SPM021301Form model) {

        // 过滤出BoRelease为"Y"的对象
        Set<Long> idSet = model.getContent().stream()
                                              .filter(obj -> CommonConstants.CHAR_Y.equals(obj.getBoRelease()))
                                              .map(SPM021301BO::getOrderId)
                                              .collect(Collectors.toSet());

        List<SalesOrderVO> salesOrderVOs = spm0213Service.findBySalesOrderIdIn(idSet);

        List<SalesOrderItemVO> items= spm0213Service.findBySalesOrderIds(idSet, model.getSiteId());

        Map<Long, SalesOrderVO> salesOrderMaps = salesOrderVOs.stream()
                .collect(Collectors.toMap(SalesOrderVO::getSalesOrderId, obj -> obj));

        Map<Long, List<SalesOrderItemVO>> salesOrderItemMaps = items.stream()
                .collect(Collectors.groupingBy(SalesOrderItemVO::getSalesOrderId));

        for (SPM021301BO bo : model.getContent()) {

            if(StringUtils.equals(bo.getBoRelease(), CommonConstants.CHAR_Y) && !ObjectUtils.isEmpty(bo.getOrderId())) {

                SalesOrderVO salesOrderVO = salesOrderMaps.get(bo.getOrderId());
                List<SalesOrderItemVO> salesOrderItemVOs = salesOrderItemMaps.get(bo.getOrderId());
                if(!ObjectUtils.isEmpty(salesOrderVO) && !ObjectUtils.isEmpty(salesOrderItemVOs)) {

                    spm0213Service.executeStockAllocation(salesOrderVO, salesOrderItemVOs);
                }

            }
        }
    }

    public SPM021302BO getBackOrderDetail(SPM021302Form model) {

        SPM021302BO result = new SPM021302BO();

        List<ConstantsBO> bos = constantsLogic.getConstantsData(PJConstants.SalesOrderPriorityType.class.getDeclaredFields());
        Map<String, String> orderTypeMap = bos.stream().filter(bo -> !Nulls.isNull(bo.getCodeDbid(), bo.getCodeData1())).collect(Collectors.toMap(ConstantsBO::getCodeDbid, ConstantsBO::getCodeData1));

        //初始化条件部
        SalesOrderVO salesOrderVO = spm0213Service.findSalesOrderByOrderId(model.getOrderId());
        result.setOrderId(salesOrderVO.getSalesOrderId());
        result.setOrderStatus(salesOrderVO.getOrderStatus());
        result.setOrderSourceType(salesOrderVO.getOrderSourceType());
        result.setOrderType(orderTypeMap.get(salesOrderVO.getOrderPriorityType()));
        result.setCustomerPromiseDate(salesOrderVO.getDeliveryPlanDate());
        result.setOrderNo(salesOrderVO.getOrderNo());
        result.setOrderDate(salesOrderVO.getOrderDate());
        result.setConsumer(salesOrderVO.getConsumerNmFull());
        result.setConsumerId(salesOrderVO.getCmmConsumerId());
        result.setMobilePhone(salesOrderVO.getMobilePhone());
        result.setContactContent(salesOrderVO.getBoContactContentType());
        result.setComment(salesOrderVO.getComment());
        result.setContactDate(salesOrderVO.getBoContactDate()+salesOrderVO.getBoContactTime());

        //初始化明细部
        List<SPM021303BO> content = new ArrayList<>();
        List<SalesOrderItemVO> items = spm0213Service.findSalesOrderItem(model.getOrderId(), model.getSiteId());

        for (SalesOrderItemVO item : items) {
            if (PJConstants.OrderCancelReasonTypeSub.KEY_MANUALCANCEL.equals(item.getCancelReasonType())) {
                continue;
            }
            SPM021303BO bo = getSpm021303BO(item);
            content.add(bo);
        }

        result.setContent(content);

        return result;
    }

    private static SPM021303BO getSpm021303BO(SalesOrderItemVO item) {
        SPM021303BO bo =new SPM021303BO();
        bo.setPartsId(item.getProductId());
        bo.setPartsNo(item.getProductCd());
        bo.setPartsNm(item.getProductNm());
        bo.setAllocatedPartsId(item.getAllocatedProductId());
        bo.setAllocatedPartsCd(item.getAllocatedProductCd());
        bo.setAllocatedPartsNm(item.getAllocatedProductNm());
        bo.setOrderQty(item.getOrderQty());
        bo.setAllocatedQty(item.getAllocatedQty());
        bo.setBoQty(item.getBoQty());
        bo.setOnPickingQty(item.getInstructionQty());
        bo.setShippedQty(item.getShipmentQty());
        return bo;
    }

    public SPM021302BO searchPurchaseStatus(SPM021302Form model,PJUserDetails uc) {

        SPM021302BO result = new SPM021302BO();
        String requestPassword = spm0213Service.getSystemParameter(MstCodeConstants.SystemParameterType.POPROGRESSPSW).getParameterValue();
        String sysOwnerCd = spm0213Service.getSystemParameter(MstCodeConstants.SystemParameterType.YNSPRESPSYSOWNERCD).getParameterValue();

        for (SPM021303BO bo : model.getContent()) {

            String orderNo = model.getOrderNo();
            String dealerCode = uc.getDealerCode();
            String allocatedPartsNo = bo.getAllocatedPartsCd();
            LocalDate currentDate = LocalDate.now();
            String rid = currentDate.toString();

            String[][] ps = new String[][] {
                                            {ARG_ONE , requestPassword},
                                            {ARG_TWO , rid},
                                            {ARG_THREE, orderNo},
                                            {ARG_FOUR, dealerCode},
                                            {ARG_FIVE , rid},
                                            {ARG_SIX, allocatedPartsNo},
                                            {ARG_SEVEN, sysOwnerCd}
                                    };

            String response = spm0213Service.searchPurchaseStatus(ps);
            if(StringUtils.isBlank(response)){
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00400"));
            }
            JSONArray jsonArray = JSON.parseArray(response);

            //获取数组中的第一个对象
            JSONObject jsonObject = jsonArray.getJSONObject(CommonConstants.INTEGER_ZERO);
            if(StringUtils.equals(jsonObject.getString(CHAR_STATUS), CommonConstants.CHAR_ZERO)){
                bo.setYamahaBO(jsonObject.getBigDecimal(CHAR_BO_QTY));
                bo.setYamahaAllocated(jsonObject.getBigDecimal(CHAR_ALLOCATED_QTY));
                bo.setYamahaInvoiced(jsonObject.getBigDecimal(CHAR_INVOICE_QTY));
                bo.setYamahaOnShipping(jsonObject.getBigDecimal(CHAR_ONSHIPPING_QTY));
                bo.setYamahaCancelled(jsonObject.getBigDecimal(CHAR_CANCELLED_QTY));
                bo.setAtpStatus(jsonObject.getString(CHAR_ATP_STATUS));
                bo.setTargetArrivalDate(jsonObject.getString(CHAR_TARGET_ARRIVAL_DATE));
                bo.setTargetDeliveryDate(jsonObject.getString(CHAR_TARGET_DELIVERY_DATE));
            }else{
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00401"));
            }
        }

         result.setContent(model.getContent());
         return result;
    }


}
