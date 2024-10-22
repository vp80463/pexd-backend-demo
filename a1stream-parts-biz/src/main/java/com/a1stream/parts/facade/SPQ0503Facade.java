package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.parts.SPQ050301BO;
import com.a1stream.domain.form.parts.SPQ050301Form;
import com.a1stream.parts.service.SPQ0503Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Component
public class SPQ0503Facade {

    @Resource
    private SPQ0503Service spq0503Service;

    public List<SPQ050301BO> findPartsMIList(SPQ050301Form model) {

        //如果输入point不存在，提示用户
        if(ObjectUtils.isEmpty(model.getPointCd())&& !ObjectUtils.isEmpty(model.getPoint())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        List<SPQ050301BO> partsMIList = spq0503Service.retrievePartsMIList(model);

        //计算字段
        for(SPQ050301BO spq050301bo : partsMIList) {

            BigDecimal shipmentAmt = spq050301bo.getShipmentAmt();
            BigDecimal shipmentCost = spq050301bo.getShipmentCost();
            BigDecimal returnAmt = spq050301bo.getReturnAmt();
            BigDecimal returnCost = spq050301bo.getReturnCost();
            BigDecimal allocateLine = new BigDecimal(spq050301bo.getAllocateLine());
            BigDecimal soLine = new BigDecimal(spq050301bo.getSoLine());

            // 计算profit: shipment_amount - shipment_cost - return_amount + return_cost
            BigDecimal profit = shipmentAmt.subtract(shipmentCost).subtract(returnAmt).add(returnCost);
            // 计算allocateRate: allocated_line / so_line
            BigDecimal allocateRate = allocateLine.divide(soLine,2,RoundingMode.HALF_UP);//.setScale(2, RoundingMode.HALF_UP)

            // 计算profitRate: Profit / (shipment_cost-return_cost) -如果除数不为零
            if(shipmentCost.subtract(returnCost).compareTo(BigDecimal.ZERO) != 0) {

                spq050301bo.setProfitRate(profit.divide((shipmentCost.subtract(returnCost)),2,RoundingMode.HALF_UP));
            }else {

                spq050301bo.setProfitRate(BigDecimal.ZERO);
            }

            spq050301bo.setAllocateRate(allocateRate);
            spq050301bo.setProfit(profit);
        }

        return partsMIList;
    }
}
