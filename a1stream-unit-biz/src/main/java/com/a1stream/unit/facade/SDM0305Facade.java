package com.a1stream.unit.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.GenderType;
import com.a1stream.common.constants.PJConstants.SalesOrderStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.unit.SDM030501BO;
import com.a1stream.domain.form.unit.SDM030501Form;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.unit.service.SDM0305Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Employee Instruction
*
* mid2303
* 2024年10月9日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/10/09   Ruan Hansheng New
*/
@Component
public class SDM0305Facade {

    @Resource
    private SDM0305Service sdm0305Service;

    @Resource
    private HelperFacade helperFacade;

    public List<SDM030501BO> getEmployeeInstructionList(SDM030501Form form) {

        List<SDM030501BO> resultList = sdm0305Service.getEmployeeInstructionList(form);
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(SalesOrderStatus.CODE_ID, GenderType.CODE_ID);
        for (SDM030501BO bo : resultList) {
            bo.setOrderStatus(codeMap.get(bo.getOrderStatus()));
            bo.setGender(codeMap.get(bo.getGender()));
        }
        return resultList;
    }

    public SDM030501Form checkFile(SDM030501Form form) {

        List<SDM030501BO> importList = form.getImportList();
        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        List<String> provinceNmList = importList.stream().map(SDM030501BO::getProvince).toList();
        List<CmmGeorgaphyVO> provinceVOList = sdm0305Service.getCmmGeographyVOList(provinceNmList);
        Map<String, CmmGeorgaphyVO> provinceVOMap = provinceVOList.stream().collect(Collectors.toMap(CmmGeorgaphyVO::getGeographyNm, Function.identity()));

        List<String> districtNmList = importList.stream().map(SDM030501BO::getDistrict).toList();
        List<CmmGeorgaphyVO> districtVOList = sdm0305Service.getCmmGeographyVOList(districtNmList);
        Map<String, CmmGeorgaphyVO> districtVOMap = districtVOList.stream().collect(Collectors.toMap(CmmGeorgaphyVO::getGeographyNm, Function.identity()));

        List<String> employeeCdList = importList.stream().map(SDM030501BO::getEmployeeCd).toList();

        for (SDM030501BO item : importList) {

            StringBuilder errorMsg = new StringBuilder();
            List<String> error = new ArrayList<>();

            if (null != item.getMobilePhone() && item.getMobilePhone().length() > CommonConstants.INTEGER_TEN) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10330", new Object[] { item.getMobilePhone() }));
            }

            if (null != item.getMobilePhone() && item.getMobilePhone().length() < CommonConstants.INTEGER_TEN) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10331", new Object[] { item.getMobilePhone() }));
            }

            if (StringUtils.isBlankText(item.getMobilePhone())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10326", new Object[] { CodedMessageUtils.getMessage("label.mobilephone")
                                                                          + CommonConstants.CHAR_LEFT_PARENTHESIS
                                                                          + item.getMobilePhone()
                                                                          + CommonConstants.CHAR_RIGHT_PARENTHESIS }));
            }

            CmmGeorgaphyVO provinceVO = provinceVOMap.get(item.getProvince());
            if (null == provinceVO) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303", new Object[] { CodedMessageUtils.getMessage("label.province")
                        , item.getProvince()
                        , CodedMessageUtils.getMessage("label.tableGeorgaphy") }));
            } else {
                item.setProvinceId(provinceVO.getGeographyId());
            }

            CmmGeorgaphyVO districtVO = districtVOMap.get(item.getDistrict());
            if (null == districtVO) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303", new Object[] { CodedMessageUtils.getMessage("label.city")
                        , item.getDistrict()
                        , CodedMessageUtils.getMessage("label.tableGeorgaphy") }));
            } else {
                item.setDistrictId(districtVO.getGeographyId());
            }

            if (null != provinceVO && null != districtVO && !districtVO.getParentGeographyId().equals(provinceVO.getGeographyId())) {
                errorMsg.append("City and province mismatch! ");
            }

            if (null == item.getEmployeeDiscount()) {
                errorMsg.append("AP44 price is required.");
            }

            if (null != item.getEmployeeDiscount() && (item.getEmployeeDiscount().compareTo(BigDecimal.ZERO) < 0 || item.getEmployeeDiscount().compareTo(BigDecimal.ONE) > 0)) {
                errorMsg.append("APP4 Price is not an effective discount rate.");
            }

            if (employeeCdList.indexOf(item.getEmployeeCd()) != employeeCdList.lastIndexOf(item.getEmployeeCd())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00301", new Object[] { CodedMessageUtils.getMessage("label.employeeCode")
                        + CommonConstants.CHAR_LEFT_PARENTHESIS
                        + item.getEmployeeCd()
                        + CommonConstants.CHAR_RIGHT_PARENTHESIS }));
            }

            String birthDay = item.getBirthDay();
            if (StringUtils.isNotBlankText(birthDay)) {
                String birthYear = birthDay.substring(0, 4);
                String birthDate = birthDay.substring(4);
                item.setBirthYear(birthYear);
                item.setBirthDate(birthDate);
            }

            item.setErrorMessage(errorMsg.toString());
            if (errorMsg.length() > 0) {
                error.add(errorMsg.toString());
            }
            item.setError(error);
        }
        return form;
    }

    public SDM030501Form getUserType(SDM030501Form form) {

        SysUserAuthorityVO sysUserAuthorityVO = sdm0305Service.getSysUserAuthorityVO(form.getUserId());
        String roleList = sysUserAuthorityVO.getRoleList();

        JSONArray jsonArray = JSON.parseArray(roleList);
        Set<String> roleIdSet = new HashSet<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String roleId = jsonObject.getString("roleId");
            roleIdSet.add(roleId);
        }

        boolean containsACCT = roleIdSet.contains("1") || roleIdSet.contains("2") || roleIdSet.contains("22") || roleIdSet.contains("23");
        boolean containsYMVNSD = roleIdSet.contains("17") || roleIdSet.contains("38") || roleIdSet.contains("10") || roleIdSet.contains("31");

        if (!containsACCT && containsYMVNSD) {
            form.setUserType("YMVNSD");
        } else {
            form.setUserType("DEALER");
        }
        return form;
    }

    public SDM030501Form confirm(SDM030501Form form) {

        return sdm0305Service.confirm(form);
    }

    public SDM030501Form cancel(SDM030501Form form) {

        return sdm0305Service.cancel(form);
    }
}
