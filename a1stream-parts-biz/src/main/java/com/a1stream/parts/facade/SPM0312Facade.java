package com.a1stream.parts.facade;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.InOutType;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.bo.parts.SPM031201BO;
import com.a1stream.domain.form.parts.SPM031201Form;
import com.a1stream.domain.vo.*;
import com.a1stream.parts.service.SPM0312Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPM0312Facade {

    @Resource
    private SPM0312Service spm0312Service;

    @Resource InventoryManager inventoryManager;

    public SPM031201BO getLocationQty(SPM031201Form model,String siteId) {

        SPM031201BO bo = new SPM031201BO();

        //如果pointId和partsId有值即可获得冻结数量
        if ((!ObjectUtils.isEmpty(model.getPointId()) && (!ObjectUtils.isEmpty(model.getPartsId())))) {
            ProductStockStatusVO productStockStatusVO = spm0312Service.findProductStockStatus(siteId, model.getPointId(), model.getPartsId(), PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid());
            if (!ObjectUtils.isEmpty(productStockStatusVO)) {
                bo.setFrozenQty(productStockStatusVO.getQuantity());
            }

            ProductInventoryVO productInventoryVO = spm0312Service.findProductInventoryByFlag(model.getPointId(),model.getPartsId(),siteId,CommonConstants.CHAR_Y);
            if(!ObjectUtils.isEmpty(productInventoryVO)) {
                LocationVO locationVO = spm0312Service.findLocationCdByLocationId(productInventoryVO.getLocationId(),siteId);
                bo.setMainLocation(locationVO.getLocationCd());
                bo.setToLocation(locationVO.getLocationCd());
                bo.setToLocationId(locationVO.getLocationId());
                bo.setReleaseType(PJConstants.ReleaseType.LOCATIONSTORAGE);

            }

            //如果fromLocationId有值即可获得原货位货物数量
            if(!ObjectUtils.isEmpty(model.getFromLocationId())) {
                ProductInventoryVO fromLocationvo = spm0312Service.findProductInventory(model.getPointId(), model.getPartsId(), siteId, model.getFromLocationId());
                if (!ObjectUtils.isEmpty(fromLocationvo)) {
                    bo.setFromLocationQty(fromLocationvo.getQuantity());
                }
            }
        }


        //没有值则设0
        if(ObjectUtils.isEmpty(bo.getFrozenQty())) {
            bo.setFrozenQty(new BigDecimal(CommonConstants.INTEGER_ZERO));
        }

        if(ObjectUtils.isEmpty(bo.getFromLocationQty())) {
            bo.setFromLocationQty(new BigDecimal(CommonConstants.INTEGER_ZERO));
        }

        return bo;
    }

    public void partsForzenStockReleaseConfirm(SPM031201Form model,PJUserDetails uc) {

        //check
        this.validateData(model,uc);

        ProductInventoryVO fromInventoryVO = spm0312Service.findProductInventoryByLocationId(model.getPointId(), model.getPartsId(), uc.getDealerCode(), model.getFromLocationId());
        ProductInventoryVO toInventoryVO = spm0312Service.findProductInventoryByLocationId(model.getPointId(), model.getPartsId(), uc.getDealerCode(), model.getToLocationId());
        InventoryTransactionVO inventoryTransactionVO = null;

        //类型是Scrapping时，还需要新增库存调整记录
        if (StringUtils.equals(model.getReleaseType(), PJConstants.ReleaseType.SCRAPPING)) {

            //获取当前库存数量
            List<String> statusType = new ArrayList<>();
            statusType.add(PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
            statusType.add(PJConstants.SpStockStatus.ONSERVICE_QTY.getCodeDbid());
            statusType.add(PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid());
            statusType.add(PJConstants.SpStockStatus.ALLOCATED_QTY.getCodeDbid());
            statusType.add(PJConstants.SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
            statusType.add(PJConstants.SpStockStatus.ONPICKING_QTY.getCodeDbid());
            statusType.add(PJConstants.SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
            List<ProductStockStatusVO> productStockStatusVOs = spm0312Service.findProductStockStatusIn(uc.getDealerCode(), model.getPointId(), model.getPartsId(), statusType);
            BigDecimal stockQuantity = productStockStatusVOs.stream().map(ProductStockStatusVO :: getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);

            //新增库存调整记录
            ProductCostVO productCostVO = spm0312Service.findProductCostByProductId(model.getPartsId(), PJConstants.CostType.AVERAGE_COST, uc.getDealerCode());
            inventoryTransactionVO = inventoryManager.generateInventoryTransactionVO(uc.getDealerCode(), InOutType.OUT, model.getPointId(), null, model.getPointId(), model.getPartsId(), model.getPartsCd(), model.getPartsNm(), PJConstants.InventoryTransactionType.DISPOSAL.getCodeDbid(), model.getReleaseQty(), stockQuantity, productCostVO.getCost(), null, null, null, null, model.getFromLocationId(), productCostVO, null, model.getPersonId(), model.getPersonNm(), PJConstants.ProductClsType.PART.getCodeDbid());
        }

        //调用共通方法
        spm0312Service.doFrozenStockRelease( model
                                           , fromInventoryVO
                                           , toInventoryVO
                                           , inventoryTransactionVO);

    }

    void validateData(SPM031201Form model,PJUserDetails uc) {

        //检查pointId是否存在
        if(ObjectUtils.isEmpty(model.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPointCd(), CodedMessageUtils.getMessage("label.tableFacilityInfo")}));
        }

        //查看partsId是否存在
        if(ObjectUtils.isEmpty(model.getPartsId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), model.getPartsCd(), CodedMessageUtils.getMessage("label.productInformation")}));
        }

        //检查releaseQty合法性
        if(model.getReleaseQty().compareTo(model.getFromLocationQty()) == CommonConstants.INTEGER_ONE) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00203", new String[] {CodedMessageUtils.getMessage("label.releaseQty"), CodedMessageUtils.getMessage("label.formLocationQty")}));
        }

        //releaseQty需要大于0
        if(model.getReleaseQty().compareTo(BigDecimal.ZERO) <= CommonConstants.INTEGER_ZERO) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[] {CodedMessageUtils.getMessage("label.releaseQty"), CommonConstants.CHAR_ZERO }));
        }

        //判断fromLocation是否存在
        if(ObjectUtils.isEmpty(model.getFromLocationId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.fromLocation"), model.getFromLocation(),CodedMessageUtils.getMessage("label.tableLocationInfo") }));
        }

        //判断toLocation是否存在
        if(StringUtils.equals(model.getReleaseType(), PJConstants.ReleaseType.LOCATIONSTORAGE) && ObjectUtils.isEmpty(model.getToLocationId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.toLocation"), model.getToLocation(),CodedMessageUtils.getMessage("label.tableLocationInfo") }));
        }
    }
}
