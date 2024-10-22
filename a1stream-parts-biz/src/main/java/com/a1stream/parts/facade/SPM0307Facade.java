/**
 *
 */
package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.parts.SPM030701BO;
import com.a1stream.domain.form.parts.SPM030701Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.parts.service.SPM0307Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPM0307Facade {

    @Resource
    private SPM0307Service spm0307Service;

    public SPM030701BO getLocationQty(SPM030701Form model) {

        SPM030701BO bo = new SPM030701BO();

        if ((!ObjectUtils.isEmpty(model.getPointId()) && (!ObjectUtils.isEmpty(model.getFromLocationId())) && (!ObjectUtils.isEmpty(model.getPartsId())))) {
            ProductInventoryVO fromLocationvo = spm0307Service.findProductInventory(model.getPointId(), model.getPartsId(), model.getSiteId(), model.getFromLocationId());
            if (!ObjectUtils.isEmpty(fromLocationvo)) {
                bo.setCurrentStockQty(fromLocationvo.getQuantity());
            }
        }

        if(!ObjectUtils.isEmpty(model.getPartsId())){
            ProductCostVO costVO = spm0307Service.findByProductIdAndCostTypeAndSiteId(model);
            if(!ObjectUtils.isEmpty(costVO)) {
                bo.setAverageCost(costVO.getCost());
            }else {
                bo.setAverageCost(BigDecimal.ZERO);
            }
        }

        //没有值则设0
        if(ObjectUtils.isEmpty(bo.getCurrentStockQty())) {
            bo.setCurrentStockQty(new BigDecimal(CommonConstants.INTEGER_ZERO));
        }

        return bo;
    }

    public void partsStockAdjustmentConfirm(SPM030701Form model) {

        this.validateData(model);
        spm0307Service.doPartsStockAdjustment(model);

    }


    void validateData(SPM030701Form model) {

        //检查pointId是否存在
        if(ObjectUtils.isEmpty(model.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPointCd(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        //查看partsId是否存在
        if(ObjectUtils.isEmpty(model.getPartsId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), model.getPartsCd(), CodedMessageUtils.getMessage("label.productInformation")}));
        }

        //调整数量需要大于0
        if(model.getAdjustmentQty().compareTo(BigDecimal.ZERO) <= CommonConstants.INTEGER_ZERO) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[] {CodedMessageUtils.getMessage("label.adjustmentQuantity"), CommonConstants.CHAR_ZERO }));
         }

        //FromLocation是否存在
        if(ObjectUtils.isEmpty(model.getFromLocationId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.location"), model.getFromLocation(),CodedMessageUtils.getMessage("label.tableLocationInfo") }));
        }

        if(StringUtils.equals(model.getCheckForMinus(), CommonConstants.CHAR_Y)) {

            //toLocation是否存在
            if(StringUtils.equals(model.getAdjustmentType(), PJConstants.StockAdjustmentType.ADJUSTMENTTOFROZEN.getCodeDbid()) && ObjectUtils.isEmpty(model.getToLocationId())){
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.toLocation"), model.getToLocation(),CodedMessageUtils.getMessage("label.tableLocationInfo") }));
            }

            //库存减的情况下，调整数量需要大于库存数量
            if(model.getAdjustmentQty().compareTo(model.getCurrentStockQty()) == CommonConstants.INTEGER_ONE) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00203", new String[] {CodedMessageUtils.getMessage("label.adjustmentQuantity"), CodedMessageUtils.getMessage("label.currentStock")}));
            }

        }else {

            //产品成本需要大于0
            if(model.getPartsCost().compareTo(BigDecimal.ZERO) <= CommonConstants.INTEGER_ZERO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[] {CodedMessageUtils.getMessage("label.partsCost"), CommonConstants.CHAR_ZERO }));
             }
        }
    }

    public SPM030701BO getLocationInfo(SPM030701Form model) {

        SPM030701BO bo = new SPM030701BO();

        //主货位Id存在时，从主货位获取数据
        ProductInventoryVO inventoryVO = null;
        LocationVO locationVO = null;
        if (!ObjectUtils.isEmpty(model.getMainLocationId())) {
            inventoryVO = spm0307Service.findProductInventory(model.getPointId(), model.getPartsId(), model.getSiteId(), model.getFromLocationId());
            locationVO = spm0307Service.findLocation(model.getMainLocationId());
        }else {
            //当主货位不存在时，从其他货位中找到数量最大的货位
            List<ProductInventoryVO> inventoryList = spm0307Service.findProductInventoryByProductId(model.getPointId(), model.getPartsId(), model.getSiteId());
            inventoryVO = inventoryList.stream()
                                       .max(Comparator.comparing(ProductInventoryVO::getQuantity))
                                       .orElse(null);
            if (!ObjectUtils.isEmpty(inventoryVO)) {
                locationVO = spm0307Service.findLocation(inventoryVO.getLocationId());
            }
        }

        if (!ObjectUtils.isEmpty(inventoryVO)) {
            bo.setFromLocationId(inventoryVO.getLocationId());
            bo.setCurrentStockQty(inventoryVO.getQuantity());
        }
        if (!ObjectUtils.isEmpty(locationVO)) {
            bo.setFromLocation(locationVO.getLocationCd());
            bo.setFromBinTypeId(locationVO.getBinTypeId());
        }

        //没有值则设0
        if(ObjectUtils.isEmpty(bo.getCurrentStockQty())) {
            bo.setCurrentStockQty(new BigDecimal(CommonConstants.INTEGER_ZERO));
        }

        return bo;
    }

}
