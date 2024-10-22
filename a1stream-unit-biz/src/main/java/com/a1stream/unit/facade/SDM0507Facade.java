package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDM050701BO;
import com.a1stream.domain.form.unit.SDM050701Form;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.unit.service.SDM0507Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Waiting Screen
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/29   Wang Nan      New
*/

@Component
public class SDM0507Facade {

    @Resource
    private SDM0507Service sdm0507Service;

    public Page<SDM050701BO> getWaitingScreenList(SDM050701Form form) {

        //检索前Check
        this.validateBeforeRetrieve(form);

        // 判断当前用户属于666N中的SD或财务
        this.setUserFlag(form);

        return sdm0507Service.getWaitingScreenList(form);
    }

    private void validateBeforeRetrieve(SDM050701Form form) {

        //1.period只能查询一年内的数据
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

        //2.promotion存在性Check
        if(StringUtils.isNotBlank(form.getPromotion()) && ObjectUtils.isEmpty(form.getPromotionId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.promotion"),
                                             form.getPromotion(),
                                             CodedMessageUtils.getMessage("label.tablePromotionList")}));
        }

        //3.dealer存在性Check
        if (StringUtils.isNotBlank(form.getDealer()) && Nulls.isNull(form.getDealerId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.dealer"),
                                             form.getDealer(),
                                             CodedMessageUtils.getMessage("label.tableOrganizationInfo")}));
        }

        //4.model存在性Check
        if(StringUtils.isNotBlank(form.getModel()) && ObjectUtils.isEmpty(form.getModelId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.model"),
                                             form.getModel(),
                                             CodedMessageUtils.getMessage("label.productInformation")}));
        }

    }

    public void confirm(SDM050701Form form) {

        List<SDM050701BO> updList = form.getGridData();

        if (updList.isEmpty()) {
            return;
        }

        //保存
        this.save(updList);

    }


    private void save(List<SDM050701BO> list) {

        Map<Long, CmmPromotionOrderVO> map = new HashMap<>();
        List<CmmPromotionOrderVO> updCmmPromotionOrderVOList = new ArrayList<>();

        List<Long> promotionOrderIds = list.stream().map(SDM050701BO::getPromotionOrderId).toList();
        List<CmmPromotionOrderVO> cmmPromotionOrderVOList = sdm0507Service.getCmmPromotionOrderVOList(promotionOrderIds);

        if (!cmmPromotionOrderVOList.isEmpty()) {

            map = cmmPromotionOrderVOList.stream().collect(Collectors.toMap(CmmPromotionOrderVO::getPromotionListId, vo -> vo));

        }

        for (SDM050701BO bo : list) {

            CmmPromotionOrderVO cmmPromotionOrderVO = map.get(bo.getPromotionOrderId());

            if (!Nulls.isNull(cmmPromotionOrderVO)) {

                cmmPromotionOrderVO.setCanEnjoyPromotion(bo.getCanEnjoyFlag());
                updCmmPromotionOrderVOList.add(cmmPromotionOrderVO);
            }
        }
        sdm0507Service.updateCmmPromotionOrderList(updCmmPromotionOrderVOList);
    }

    private void setUserFlag(SDM050701Form form) {

        //判断当前用户是属于666N中的SD或者财务
        SysUserAuthorityVO sysUserAuthorityVO = sdm0507Service.findSysUserAuthority(form.getUserId());

        if(!Nulls.isNull(sysUserAuthorityVO)) {
            form.setUserFlag(Boolean.TRUE);
        }
    }

}
