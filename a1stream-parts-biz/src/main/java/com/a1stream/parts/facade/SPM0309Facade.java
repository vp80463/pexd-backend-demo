package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM030901BO;
import com.a1stream.domain.form.parts.SPM030901Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.a1stream.parts.service.SPM0309Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述: 库存盘点时真实库存录入
*
* @author mid2215
*/
@Component
public class SPM0309Facade {

    @Resource
    private SPM0309Service spm0309Service;

    /**
     * 根据条件查找部品真实库存
     */
    public List<SPM030901BO> findPartsActualStockList(SPM030901Form model, String siteId) {

        model.setSiteId(siteId);

        // 查看partsId是否存在
        if(ObjectUtils.isEmpty(model.getPartsId())&& StringUtils.isNotBlank(model.getParts())){
             throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), model.getParts(), CodedMessageUtils.getMessage("label.productInformation")}));
        }

        // 校验seqNoFrom是否大于seqNoTo
        checkSeqNoValidation(model.getSeqNoFrom(),model.getSeqNoTo());

        // 校验该仓库是否在盘点
        checkStackTakingStatus(model);

        List<SPM030901BO> detailModelList = spm0309Service.findPartsActualStockList(model);

        // 取到所有Workzone ,格式:Map<workzoneId，workzoneName>
        List<WorkzoneVO> workzonesList = spm0309Service.findWorkzoneBySiteId(siteId);
        Map<Long,String> workzoneMap = workzonesList.stream().collect(Collectors.toMap(WorkzoneVO::getWorkzoneId, WorkzoneVO::getDescription));

        // 取到所有Location ,格式:Map<locationId，locationCd>
        List<LocationVO> locationList = spm0309Service.findLocationBysiteId(siteId);
        Map<Long,String> locationMap = locationList.stream().collect(Collectors.toMap(LocationVO::getLocationId, LocationVO::getLocationCd));
        Map<Long,String> locationTypeMap = locationList.stream().collect(Collectors.toMap(LocationVO::getLocationId, LocationVO::getLocationType));

        // 给Id做映射
        for(SPM030901BO detailModel : detailModelList) {

            detailModel.setWorkzone(workzoneMap.get(detailModel.getWorkzoneId()));
            detailModel.setLocation(locationMap.get(detailModel.getLocationId()));
            // 用于与页面上的ActualQty做比对，如果不相等则push到updateList
            detailModel.setActualQtyBefore(detailModel.getActualQty());
            detailModel.setLocationTypeCode(locationTypeMap.get(detailModel.getLocationId()));
        }

        return detailModelList;
    }

    /**
     * 删除部品真实库存
     */
    public void deletePartsActualStock(SPM030901Form model, String siteId) {

        //检查该仓库是否在盘点
        model.setSiteId(siteId);
        checkStackTakingStatus(model);

        ProductStockTakingVO productStockTakingVO = spm0309Service.getByProductStockTakingId(model.getProductStockTakingId());

        spm0309Service.deletePartsActualStock(productStockTakingVO);
    }

    /**
     * 新建或更新部品真实库存
     */
    public void saveOrUpdatePartsActualStock(SPM030901Form model, PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());

        // 新建或更新部品真实库存前验证
        this.validateNewOrModifyLocationInfo(model);

        ProductStockTakingVO productStockTakingVO = this.buildProductStockTakingVO(model, uc);

        spm0309Service.saveOrUpdatePartsActualStock(productStockTakingVO);
    }

    /**
     * 新增或修改实际库存前校验
     */
    private void validateNewOrModifyLocationInfo(SPM030901Form model) {

        //检查该仓库是否在盘点
        checkStackTakingStatus(model);

        ProductStockTakingVO productStockTakingVOs = spm0309Service.findByPorudctIdAndLocationId(model.getPartsId(), model.getLocationId());

        // 如果parts 和 Location已经存在，则提示用户
        if(productStockTakingVOs != null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00305", new String[] {CodedMessageUtils.getMessage("label.location")
                                                                                                   , model.getLocation()
                                                                                                   , CodedMessageUtils.getMessage("label.parts")
                                                                                                   , model.getParts()}));
        }

        // 如果实际库存小于等于零，则提示用户
        if(model.getActualQty().compareTo(BigDecimal.ZERO) < 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[] {CodedMessageUtils.getMessage("label.actualQty")
                                                                                                   , CommonConstants.CHAR_ZERO}));
        }

        // 如果cost小于等于零，则提示用户
        if(model.getCurrentAverageCost().compareTo(BigDecimal.ZERO) < 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[] {CodedMessageUtils.getMessage("label.cost")
                                                                                                   , CommonConstants.CHAR_ZERO}));
        }

        // 如果LocationType 不为 NORMAL 或 TENTATIVE ,则提示用户
        if(!StringUtils.equals(model.getLocationTypeCode(), PJConstants.LocationType.TENTATIVE.getCodeDbid())
        && !StringUtils.equals(model.getLocationTypeCode(), PJConstants.LocationType.NORMAL.getCodeDbid())) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00159", new String[] {CodedMessageUtils.getMessage("label.location")
                                                                                                   , model.getLocation()}));
        }
    }

    /**
     * 批量修改真实库存（明细）
     */
    public void editPartsActualStockList(SPM030901Form model,String siteId) {

        model.setSiteId(siteId);

        //检查该仓库是否在盘点
        checkStackTakingStatus(model);

        //将修改的数据转换为key为Id，value为自身的Map
        List<ProductStockTakingVO> updateVoList = new ArrayList<>();
        List<SPM030901BO> updateList = model.getStockData().getUpdateRecords();
        Map<Long, SPM030901BO> updateMap = updateList.stream().collect(Collectors.toMap(SPM030901BO::getProductStockTakingId, c -> c));

        if (CollectionUtils.isNotEmpty(updateList)) {

            //获取待修改原数据随后修改
            List<ProductStockTakingVO> updateStockListVO = spm0309Service.findByProductStockTakingIdIn(updateMap.keySet());
            for (ProductStockTakingVO vo : updateStockListVO) {
                SPM030901BO updateModel = updateMap.get(vo.getProductStockTakingId());
                vo.setActualQty(updateModel.getActualQty());
                vo.setInputFlag(CommonConstants.CHAR_Y);
                updateVoList.add(vo);
            }
        }

        spm0309Service.editPartsActualStockList(updateVoList);
    }

    /**
     * 创建一个ProductStockTakingVO
     */
    private ProductStockTakingVO buildProductStockTakingVO(SPM030901Form model, PJUserDetails uc) {

        ProductStockTakingVO productStockTakingVO;

        if(model.getProductStockTakingId()!=null) {
            productStockTakingVO = spm0309Service.getByProductStockTakingId(model.getProductStockTakingId());
        }else {

            productStockTakingVO = new ProductStockTakingVO();
            productStockTakingVO.setSiteId(uc.getDealerCode());
            productStockTakingVO.setFacilityId(model.getPointId());
            productStockTakingVO.setRangeType(model.getRangeType());
            // 取到最大的seqNo并加1，作为新数据的SeqNo
            productStockTakingVO.setSeqNo(spm0309Service.getMaxSeqNo(uc.getDealerCode(), model.getPointId())+1);
            productStockTakingVO.setExpectedQty(new BigDecimal(CommonConstants.INTEGER_ZERO));
            productStockTakingVO.setWorkzoneId(model.getWzId());
            productStockTakingVO.setLocationId(model.getLocationId());
            productStockTakingVO.setInputFlag(CommonConstants.CHAR_Y);
            productStockTakingVO.setNewFoundFlag(CommonConstants.CHAR_Y);
            productStockTakingVO.setStartedDate(model.getStartedDate());
            productStockTakingVO.setStartedTime(model.getStartedTime());
            productStockTakingVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        }

        productStockTakingVO.setProductId(model.getPartsId());
        productStockTakingVO.setActualQty(model.getActualQty());
        productStockTakingVO.setPicCd(uc.getUserId());
        productStockTakingVO.setPicNm(uc.getUsername());
        productStockTakingVO.setCurrentAverageCost(model.getCurrentAverageCost());

        return productStockTakingVO;
    }

    /**
     * 校验当前point是否正在盘点
     */
    private void checkStackTakingStatus(SPM030901Form model) {

        SystemParameterVO systemParameterVO = spm0309Service.findSystemParameterVOList(model.getSiteId()
                                                                                     , model.getPointId()
                                                                                     , MstCodeConstants.SystemParameterType.STOCKTAKING
                                                                                     , CommonConstants.CHAR_ONE);

        if(systemParameterVO == null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143", new String[] {}));
        }
    }

    /**
     * 校验seqNoFrom是否大于seqNoTo
     */
    private void checkSeqNoValidation(BigDecimal seqNoFrom, BigDecimal seqNoTo) {

        if(seqNoFrom.compareTo(seqNoTo) > 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[] {CodedMessageUtils.getMessage("label.seqNo")
                                                                                                   , seqNoFrom.toString()}));
        }
    }
}
