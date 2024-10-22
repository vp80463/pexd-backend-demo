package com.a1stream.master.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM020201BO;
import com.a1stream.domain.bo.master.CMM020202BO;
import com.a1stream.domain.bo.master.CMM020202GridBO;
import com.a1stream.domain.form.master.CMM020202Form;
import com.a1stream.domain.vo.CmmPersonFacilityVO;
import com.a1stream.domain.vo.CmmPersonVO;
import com.a1stream.master.service.CMM0202Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class CMM0202Facade {

    @Resource
    private CMM0202Service cmm0202Service;

    public List<CMM020201BO> getEmployeeInfoListBySiteId(String siteId) {

        return cmm0202Service.getEmployeeInfoListBySiteId(siteId);
    }

    public CMM020202BO getEmployeeDetail(Long personId, String siteId) {

        return cmm0202Service.getEmployeeDetail(personId, siteId);
    }

    public void saveOrUpdateEmployee(CMM020202Form model, String siteId) {

        // 去除重复数据
        this.modifyPointDetailList(model);

        // 更新前验证
        this.validateNewOrModifyEmployeeInfo(model, siteId);

        CmmPersonVO cmmPersonVO = this.buildCmmPersonVO(model, siteId);

        List<CMM020202GridBO> pointList = this.buildCmmPersonFacilityVO(model, siteId, cmmPersonVO);

        // 获取员工和Point关联的全删除对象
        List<CmmPersonFacilityVO> removePointList = this.getRemovePointList(model, siteId);

        cmm0202Service.saveOrUpdateEmployee(cmmPersonVO, pointList, removePointList);
    }

    private List<CmmPersonFacilityVO> getRemovePointList(CMM020202Form model, String siteId) {

        List<CmmPersonFacilityVO> removePointList = new ArrayList<>();

        if(model.getPersonId() != null) {

            removePointList = cmm0202Service.getRemovePointListByPersonId(siteId, model.getPersonId());
        }

        return removePointList;
    }

    private List<CMM020202GridBO> buildCmmPersonFacilityVO(CMM020202Form model, String siteId, CmmPersonVO cmmPersonVO) {

        List<CMM020202GridBO> pointList = model.getPointList();

        if(pointList != null && !pointList.isEmpty()) {

            pointList = pointList.stream().map(cmmPersonFacility -> {cmmPersonFacility.setPersonId(cmmPersonVO.getPersonId());
                                                                     cmmPersonFacility.setSiteId(siteId);
                                                                     return cmmPersonFacility;}).toList();
        }

        return pointList;
    }

    private CmmPersonVO buildCmmPersonVO(CMM020202Form model, String siteId) {

        CmmPersonVO cmmPersonVO = model.getPersonId() != null ? cmm0202Service.findPersonById(model.getPersonId()) : CmmPersonVO.create();

        cmmPersonVO.setSiteId(siteId);
        cmmPersonVO.setPersonCd(model.getPersonCd());
        cmmPersonVO.setPersonNm(model.getFirstNm().concat(StringUtils.isNotEmpty(model.getMiddleNm()) ? model.getMiddleNm() : CommonConstants.CHAR_BLANK).concat(model.getLastNm()));
        cmmPersonVO.setFirstNm(model.getFirstNm());
        cmmPersonVO.setMiddleNm(model.getMiddleNm());
        cmmPersonVO.setLastNm(model.getLastNm());
        cmmPersonVO.setFromDate(model.getFromDate());
        cmmPersonVO.setToDate(model.getToDate());
        cmmPersonVO.setGenderType(model.getGenderType());
        cmmPersonVO.setProvinceId(model.getProvinceId());
        cmmPersonVO.setProvinceNm(model.getProvinceNm());
        cmmPersonVO.setCityId(model.getCityId());
        cmmPersonVO.setCityNm(model.getCityNm());
        cmmPersonVO.setAddress1(model.getAddress1());
        cmmPersonVO.setAddress2(model.getAddress2());
        cmmPersonVO.setPostCode(model.getPostCode());
        cmmPersonVO.setPersonType(model.getPersonType());
        cmmPersonVO.setContactTel(model.getContactTel());
        cmmPersonVO.setFaxNo(model.getFaxNo());

        return cmmPersonVO;
    }

    private void validateNewOrModifyEmployeeInfo(CMM020202Form model, String siteId) {

        // 新增员工时
        if(model.getPersonId() == null) {

            // 员工cd在当前site不能重复
            if(cmm0202Service.findPersonExistByCode(siteId, model.getPersonCd())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[] {CodedMessageUtils.getMessage("label.employee"), model.getPersonCd(), CodedMessageUtils.getMessage("title.employeeInfoList_01")}));
            }
        }

        // 更新员工信息时
        if(model.getPersonId() != null) {

            // employeeId在DB不存在时，报错
            CmmPersonVO cmmPersonVO = cmm0202Service.findPersonById(model.getPersonId());

            if (cmmPersonVO == null) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.employee"), model.getPersonCd(), CodedMessageUtils.getMessage("title.employeeInfoList_01")}));
            }

            // 更新员工信息时员工cd在当前site不能重复
            if(cmm0202Service.findPersonExistByCodeAndNotThisPersonId(siteId, model.getPersonCd(), model.getPersonId())) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[] {CodedMessageUtils.getMessage("label.employee"), model.getPersonCd(), CodedMessageUtils.getMessage("title.employeeInfoList_01")}));
            }
        }

        // 校验pointId是否存在
        for(CMM020202GridBO gridBO : model.getPointList()) {

            if(ObjectUtils.isEmpty(gridBO.getFacilityId())) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{CodedMessageUtils.getMessage("label.point")}));
            }
        }
    }

    private void modifyPointDetailList(CMM020202Form model) {

        List<CMM020202GridBO> pointList = model.getPointList();

        if(pointList != null && !pointList.isEmpty()) {

            // 去除当前员工下所有重复的Point明细，以作为删除对象
            pointList = pointList.stream().collect(Collectors.toMap(CMM020202GridBO::getFacilityId, obj -> obj, (existing, replacement) -> existing))
                                          .values()
                                          .stream()
                                          .collect(Collectors.toList());

            model.setPointList(pointList);
        }
    }
}
