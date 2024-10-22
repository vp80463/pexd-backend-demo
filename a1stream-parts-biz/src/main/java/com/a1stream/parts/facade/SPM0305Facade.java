package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.parts.SPM030501BO;
import com.a1stream.domain.form.parts.SPM030501Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.parts.service.SPM0305Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Location Stock Movement
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
*/
@Component
public class SPM0305Facade {

    @Resource
    private SPM0305Service spm0305Service;

    private static final List<String> LOCATIONTYPE_NT_LIST = Arrays.asList(PJConstants.LocationType.NORMAL.getCodeDbid(),
                                                                           PJConstants.LocationType.TENTATIVE.getCodeDbid());

    public List<SPM030501BO> getPartsLocationList(SPM030501Form form) {

        //检索前Check
        this.check(form.getPointvl(), form.getPointId(), form.getParts(), form.getPartsId());

        return spm0305Service.getPartsLocationList(form, form.getSiteId());
    }

    public void confirmPartsLocationStockMovement(SPM030501Form form) {

        //更新前Check
        this.validateData(form);

    }

    private void validateData(SPM030501Form form) {

        List<SPM030501BO> list = form.getGridDataList();

        if (list.isEmpty()) {
            return;
        }

        BigDecimal sumMovementQty = list.stream().map(SPM030501BO::getMovementQty).reduce(BigDecimal.ZERO, BigDecimal::add);

        //根据movementQty > 0 来判断数据是否发生变更
        if (NumberUtil.le(sumMovementQty, BigDecimal.ZERO)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{
                                             CodedMessageUtils.getMessage("label.movementQty"),
                                             CommonConstants.CHAR_ZERO}));
        }

        //获取有变更的数据
        list = list.stream().filter(item -> NumberUtil.larger(item.getMovementQty(), BigDecimal.ZERO)).toList();
        form.setGridDataList(list);

        for (SPM030501BO row : list) {
            //to Location必入力check
            if (StringUtils.isBlank(row.getToLocation())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                                 CodedMessageUtils.getMessage("label.toLocation")}));
            }

            //to Location重复型checkTo Location != From Location
            if (StringUtils.equals(row.getToLocation(), row.getFromLocation())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00214", new String[]{
                                                 CodedMessageUtils.getMessage("label.toLocation"),
                                                 row.getToLocation(),
                                                 CodedMessageUtils.getMessage("label.fromLocation"),
                                                 row.getFromLocation(),
                }));
            }

            //若To Location Type ID = S006SERVICE报错
            if (StringUtils.equals(PJConstants.LocationType.SERVICE.getCodeDbid(), row.getToLocationTypeId())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10337"));
            }

            //若From Location Type ID In (S006NORMAL,S006TENTATIVE),但 To Location Type ID = S006FROZEN报错
            if (LOCATIONTYPE_NT_LIST.contains(row.getFromLocationTypeId()) && StringUtils.equals(PJConstants.LocationType.FROZEN.getCodeDbid(), row.getToLocationTypeId())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00354", new String[]{
                                                 CodedMessageUtils.getMessage("label.toLocation"),
                                                 CodedMessageUtils.getMessage("label.fromLocation")}));
            }

            //若From Location Type ID = S006FROZEN, 但To Location Type ID In (S006NORMAL,S006TENTATIVE)时报错
            if (StringUtils.equals(PJConstants.LocationType.FROZEN.getCodeDbid(), row.getFromLocationTypeId()) && LOCATIONTYPE_NT_LIST.contains(row.getToLocationTypeId())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00354", new String[]{
                                                 CodedMessageUtils.getMessage("label.fromLocation"),
                                                 CodedMessageUtils.getMessage("label.toLocation")}));
            }

            //movementQty <= stockQty
            if (NumberUtil.larger(row.getMovementQty(), row.getStockQty())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00203", new String[]{
                                                 CodedMessageUtils.getMessage("label.movementQty"),
                                                 CodedMessageUtils.getMessage("label.stockQty")}));
            }
        }

        //存在性check
        this.existToLocation(form.getSiteId(), form.getPointId(), list);

        //更新
        this.confirm(form);
    }

    private void existToLocation(String siteId, Long facilityId, List<SPM030501BO> list) {

        List<String> tolocationCds = list.stream().map(SPM030501BO::getToLocation).toList();

        //to Location存在性check
        List<LocationVO> tolocationVOList = spm0305Service.findLocationVOList(siteId, 
                                                                            facilityId,
                                                                            tolocationCds);

        List<String> tolocationCdList = tolocationVOList.stream().map(LocationVO::getLocationCd).toList();

        for (String tolocationCd: tolocationCds) {

            if (!tolocationCdList.contains(tolocationCd)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{
                                                 CodedMessageUtils.getMessage("label.toLocation"),
                                                 tolocationCd,
                                                 CodedMessageUtils.getMessage("label.tableLocationInfo")}));
            }

        }
    }

    private void check(String point, Long pointId, String parts, Long partsId) {

        //检查point
        if (StringUtils.isNotBlank(point) && Nulls.isNull(pointId)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             point,
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        //检查parts
        if (StringUtils.isNotBlank(parts) && Nulls.isNull(partsId)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             parts,
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }
    }

    private void confirm (SPM030501Form form) {

        String siteId = form.getSiteId();
        Long facilityId = form.getPointId();
        Long productId = form.getPartsId();
        Long picId = form.getPersonId();

        List<SPM030501BO> list = form.getGridDataList();

        spm0305Service.update(siteId, facilityId, productId, picId, list);
    }

}
