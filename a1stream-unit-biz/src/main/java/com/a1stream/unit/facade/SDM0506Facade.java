package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDM050601BO;
import com.a1stream.domain.form.unit.SDM050601Form;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.unit.service.SDM0506Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Update Period Maintenance
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/28   Wang Nan      New
*/

@Component
public class SDM0506Facade {

    @Resource
    private SDM0506Service sdm0506Service;

    public List<SDM050601BO> getUpdPeriodMaintList(SDM050601Form form) {

        //检索前Check
        this.validateBeforeRetrieve(form);

        // 判断当前用户属于666N中的SD或财务
        this.setUserFlag(form);

        return sdm0506Service.getUpdPeriodMaintList(form);
    }

    private void validateBeforeRetrieve(SDM050601Form form) {

        //1.判断promotion存在性Check
        if(StringUtils.isNotBlank(form.getPromotion()) && ObjectUtils.isEmpty(form.getPromotionId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.promotion"),
                                             form.getPromotion(),
                                             CodedMessageUtils.getMessage("label.tablePromotionList")}));
        }

        //2.period只能查询一年间的数据
        if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
            LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

            if (dateFrom.isAfter(dateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {
                                                    CodedMessageUtils.getMessage("label.toDate"),
                                                    CodedMessageUtils.getMessage("label.fromDate")}));
            }

            if (dateFrom.plusMonths(CommonConstants.INTEGER_TWELVE).isBefore(dateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10296", new String[] {
                                                 CodedMessageUtils.getMessage("label.orderDateFrom"),
                                                 CodedMessageUtils.getMessage("label.orderDateTo")}));
            }
        }
    }

    public void confirm(SDM050601Form form) {

        List<SDM050601BO> updList = form.getGridData();

        if (updList.isEmpty()) {
            return;
        }

        //保存前Check
        this.validateBeforeSave(updList);

        //保存
        this.save(updList);

    }

    private void validateBeforeSave(List<SDM050601BO> updList) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        for (SDM050601BO bo : updList) {

            if (StringUtils.isNotBlank(bo.getUploadEndDate()) && StringUtils.isNotBlank(bo.getExpiredDate())) {

                LocalDate uploadEndDate = LocalDate.parse(bo.getUploadEndDate(), formatter);
                LocalDate expiredDate = LocalDate.parse(bo.getExpiredDate(), formatter);

                //uploadEndDate不能比expiredDate大3个月
                if (uploadEndDate.isAfter(expiredDate.plusMonths(CommonConstants.INTEGER_THREE))) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10299", new String[] {
                                                     CodedMessageUtils.getMessage("label.expiredDate"),
                                                     CodedMessageUtils.getMessage("label.uploadEndDate")}));
                }

                if (StringUtils.isNotBlank(bo.getEffectiveDate())) {

                    LocalDate effectiveDate = LocalDate.parse(bo.getEffectiveDate(), formatter);

                    //uploadEndDate不能小于effectiveDate
                    if (uploadEndDate.isBefore(effectiveDate)) {

                        throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateBefore", new String[] {
                                                         CodedMessageUtils.getMessage("label.effectiveDate"),
                                                         CodedMessageUtils.getMessage("label.uploadEndDate")}));
                    }
                }
            }
        }
    }

    private void save(List<SDM050601BO> list) {

        Map<Long, CmmUnitPromotionListVO> map = new HashMap<>();
        List<CmmUnitPromotionListVO> updCmmUnitPromotionListVOList = new ArrayList<>();

        List<Long> promotionListIds = list.stream().map(SDM050601BO::getPromotionListId).toList();
        List<CmmUnitPromotionListVO> cmmUnitPromotionListVOList = sdm0506Service.getCmmUnitPromotionListVOList(promotionListIds);

        if (!cmmUnitPromotionListVOList.isEmpty()) {

            map = cmmUnitPromotionListVOList.stream().collect(Collectors.toMap(CmmUnitPromotionListVO::getPromotionListId, vo -> vo));

        }

        for (SDM050601BO bo : list) {

            CmmUnitPromotionListVO cmmUnitPromotionListVO = map.get(bo.getPromotionListId());

            if (!Nulls.isNull(cmmUnitPromotionListVO)) {

                cmmUnitPromotionListVO.setUploadEndDate(bo.getUploadEndDate());
                updCmmUnitPromotionListVOList.add(cmmUnitPromotionListVO);
            }
        }
        sdm0506Service.updateCmmUnitPromotionList(cmmUnitPromotionListVOList);
    }

    private void setUserFlag(SDM050601Form form) {

        //判断当前用户是属于666N中的SD或者财务
        SysUserAuthorityVO sysUserAuthorityVO = sdm0506Service.findSysUserAuthority(form.getUserId());

        if(!Nulls.isNull(sysUserAuthorityVO)) {
            form.setUserFlag(Boolean.TRUE);
        }
    }

}
