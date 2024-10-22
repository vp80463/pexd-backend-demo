package com.a1stream.master.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM040201BO;
import com.a1stream.domain.form.master.CMM040201Form;
import com.a1stream.domain.form.master.CMM040202Form;
import com.a1stream.domain.vo.CmmSectionVO;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.master.service.CMM0402Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class CMM0402Facade {

    @Resource
    private CMM0402Service cmm0402Service;

    public List<CMM040201BO> findSectionInfoInquiryList(CMM040201Form form) {

        this.validateData(form);

        return cmm0402Service.findSectionInfoInquiryList(form);
    }

    private void validateData(CMM040201Form form) {

        // 1.productCode不存在于productSection
        if(StringUtil.isNotBlank(form.getSectionCode()) && form.getSectionId() == null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.sectionCode"), form.getSectionCode(), CodedMessageUtils.getMessage("label.tableProductSection")}));
        }

        // 2.faultProductCode不存在于productFaultSection
        if(StringUtil.isNotBlank(form.getFaultSectionCode()) && form.getFaultSectionId() == null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.faultSectionCode"), form.getFaultSectionCode(), CodedMessageUtils.getMessage("label.faultSection")}));
        }
    }

    public List<CmmSymptomVO> findCmmSymptomVOList(CMM040202Form form) {

        return cmm0402Service.findCmmSymptomVOList(form);
    }

    public void confirm(CMM040202Form form) {

        validationCheck(form);

        if(form.getSectionId() == null) {
            this.newConfirm(form);
        }else {
            this.updateConfirm(form);
        }
    }

    public void newConfirm(CMM040202Form form) {

        // cmm_section Insert
        CmmSectionVO cmmSectionVO = CmmSectionVO.create();
        cmmSectionVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        cmmSectionVO.setUpdateCount(0);
        cmmSectionVO.setProductSectionCd(form.getSectionCd());
        cmmSectionVO.setProductSectionNm(form.getSectionNm());

        // cmm_symtom Insert
        CmmSymptomVO cmmSymptomVO;
        List<CmmSymptomVO> cmmSymptomVOs = new ArrayList<>();
        for(CmmSymptomVO vo : form.getTableDataList().getInsertRecords()) {

            cmmSymptomVO = new CmmSymptomVO();
            cmmSymptomVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
            cmmSymptomVO.setSymptomCd(vo.getSymptomCd());
            cmmSymptomVO.setDescription(vo.getDescription());
            cmmSymptomVO.setProductSectionId(cmmSectionVO.getProductSectionId());
            cmmSymptomVO.setUpdateCount(0);
            cmmSymptomVOs.add(cmmSymptomVO);
        }

        form.setSectionId(cmmSectionVO.getProductSectionId());

        cmm0402Service.newConfirm(cmmSectionVO, cmmSymptomVOs);
    }

    public void updateConfirm(CMM040202Form form) {

        CmmSectionVO cmmSectionVO = cmm0402Service.findCmmSectionVO(form.getSectionId());

        cmmSectionVO.setProductSectionCd(form.getSectionCd());
        cmmSectionVO.setProductSectionNm(form.getSectionNm());

        // 获取现有的 cmm_symptom 数据
        List<CmmSymptomVO> cmmSymptomVOList = cmm0402Service.findCmmSymptomVOList(form);
        Map<Long, CmmSymptomVO> cmmSymptomMap = cmmSymptomVOList.stream().collect(Collectors.toMap(CmmSymptomVO::getSymptomId, Function.identity()));

        // 处理 cmm_symptom 的更新、插入和删除
        List<CmmSymptomVO> insertcmmSymptomVOs = new ArrayList<>();
        List<CmmSymptomVO> updatecmmSymptomVOs = new ArrayList<>();
        List<Long> deletecmmSymptomIds = new ArrayList<>();

        for (CmmSymptomVO vo : form.getTableDataList().getInsertRecords()) {
            CmmSymptomVO cmmSymptomVO = new CmmSymptomVO();
            cmmSymptomVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
            cmmSymptomVO.setSymptomCd(vo.getSymptomCd());
            cmmSymptomVO.setDescription(vo.getDescription());
            cmmSymptomVO.setProductSectionId(cmmSectionVO.getProductSectionId());
            cmmSymptomVO.setUpdateCount(0);
            insertcmmSymptomVOs.add(cmmSymptomVO);
        }

        for (CmmSymptomVO vo : form.getTableDataList().getUpdateRecords()) {
            CmmSymptomVO cmmSymptomVO = cmmSymptomMap.get(vo.getSymptomId());
            if (cmmSymptomVO != null) {
                cmmSymptomVO.setSymptomCd(vo.getSymptomCd());
                cmmSymptomVO.setDescription(vo.getDescription());
                cmmSymptomVO.setProductSectionId(cmmSectionVO.getProductSectionId());
                updatecmmSymptomVOs.add(cmmSymptomVO);
            }
        }

        for (CmmSymptomVO vo : form.getTableDataList().getRemoveRecords()) {
            deletecmmSymptomIds.add(vo.getSymptomId());
        }

        form.setSectionId(cmmSectionVO.getProductSectionId());

        cmm0402Service.updateConfirm(cmmSectionVO, insertcmmSymptomVOs, updatecmmSymptomVOs, deletecmmSymptomIds);
    }

    private void validationCheck(CMM040202Form form) {

        // sectionId 存在性判断
        if(form.getSectionId() == null) {

            int checkCount = cmm0402Service.getCmmSectionCount(form);

            if(checkCount > 0) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[] {CodedMessageUtils.getMessage("label.sectionCode"), form.getSectionCd(), CodedMessageUtils.getMessage("label.tableSymptomInfo")}));
            }
        }

        // 明细不为空判断
        if(form.getDetailList().isEmpty()) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00127", new String[] {CodedMessageUtils.getMessage("label.faultSection")}));
        }
    }
}