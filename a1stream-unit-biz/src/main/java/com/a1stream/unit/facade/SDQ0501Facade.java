package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDQ050101BO;
import com.a1stream.domain.form.unit.SDQ050101Form;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.unit.service.SDQ0501Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

@Component
public class SDQ0501Facade {

    @Resource
    private SDQ0501Service sdq0501Service;

    public Page<SDQ050101BO> findPromotionMCList(SDQ050101Form form, PJUserDetails uc) {

        //查询校验
        this.check(form);

        SysUserAuthorityVO sysUserAuthorityVO =  sdq0501Service.findSysUserAuthority(uc.getUserId());

        if(sysUserAuthorityVO!=null) {
            form.setSdOrAccountFlag(CommonConstants.CHAR_TRUE);
        }

        return sdq0501Service.findPromotionMCList(form);
    }

    private void check(SDQ050101Form form) {

        //校验period时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
        LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

        if (dateFrom.isAfter(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {
                                             CodedMessageUtils.getMessage("label.toDate"),
                                             CodedMessageUtils.getMessage("label.fromDate")}));
        }

        if (dateFrom.plusMonths(CommonConstants.INTEGER_TWELVE).isBefore(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10296", new String[] {
                                             CodedMessageUtils.getMessage("label.orderDateFrom"),
                                             CodedMessageUtils.getMessage("label.orderDateTo")}));
        }

        //校验promotion
        if(StringUtils.isNotBlank(form.getPromotion()) && ObjectUtils.isEmpty(form.getPromotionId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.promotion"),
                                             form.getPromotion(),
                                             CodedMessageUtils.getMessage("label.tablePromotionList")}));
        }

        //校验dealer
        if(StringUtils.isNotBlank(form.getDealer()) && ObjectUtils.isEmpty(form.getDealerId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.dealer"),
                                             form.getDealer(),
                                             CodedMessageUtils.getMessage("label.tableCustomerInfo")}));
        }

        //校验model
        if(StringUtils.isNotBlank(form.getModel()) && ObjectUtils.isEmpty(form.getModelId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.model"),
                                             form.getModel(),
                                             CodedMessageUtils.getMessage("label.productInformation")}));
        }
    }

    public List<SDQ050101BO> findPromotionMCListExport(SDQ050101Form form, PJUserDetails uc) {

        SysUserAuthorityVO sysUserAuthorityVO =  sdq0501Service.findSysUserAuthority(uc.getUserId());

        if(sysUserAuthorityVO!=null) {
            form.setSdOrAccountFlag(CommonConstants.CHAR_TRUE);
        }

        //不需要转化日期格式为DD/MM/YYYY,后续有修改请确认
        return sdq0501Service.findPromotionMCListExport(form);
    }
}
