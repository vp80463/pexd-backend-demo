package com.a1stream.unit.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.entity.CmmLeadManagementUnit;
import com.a1stream.domain.entity.CmmLeadUpdateHistory;
import com.a1stream.domain.form.unit.SVM010603Form;
import com.a1stream.domain.repository.CmmLeadManagementUnitRepository;
import com.a1stream.domain.repository.CmmLeadUpdateHistoryRepository;
import com.a1stream.domain.vo.CmmLeadManagementUnitVO;
import com.a1stream.domain.vo.CmmLeadUpdateHistoryVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: MC Sales Lead
*
* mid2287
* 2024年8月27日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/27   Wang Nan      New
*/
@Service
public class SVM010603Service {

    @Resource
    private CmmLeadManagementUnitRepository cmmLeadUnitRepo;

    @Resource
    private CmmLeadUpdateHistoryRepository cmmLeadHistRepo;

    public Page<SVM010603BO> pageMcSalesLeadData(SVM010603Form form, String siteId, String dealerCd) {

        return cmmLeadUnitRepo.pageMcSalesLeadData(form, siteId, dealerCd);
    }

    public List<SVM010603BO> listMcSalesLeadData(SVM010603Form form, String siteId, String dealerCd) {

        return cmmLeadUnitRepo.listMcSalesLeadData(form, siteId, dealerCd);
    }

    public List<SVM010603BO> getCmmLeadUpdHistory(Long leadResultId) {

        return cmmLeadHistRepo.getCmmLeadUpdHistory(leadResultId);
    }

    public Map<Long, CmmLeadManagementUnitVO> getCmmLeadManagementUnit(List<Long> leadResultIds) {

        List<CmmLeadManagementUnitVO> result = BeanMapUtils.mapListTo(cmmLeadUnitRepo.findByLeadManagementResultIdIn(leadResultIds), CmmLeadManagementUnitVO.class);

        return result.stream().collect(Collectors.toMap(CmmLeadManagementUnitVO::getLeadManagementResultId, vo -> vo));
    }

    public void maintainData(List<CmmLeadUpdateHistoryVO> cmmLeadHistList, List<CmmLeadManagementUnitVO> cmmLeadUnitList) {

        cmmLeadHistRepo.saveInBatch(BeanMapUtils.mapListTo(cmmLeadHistList, CmmLeadUpdateHistory.class));
        cmmLeadUnitRepo.saveInBatch(BeanMapUtils.mapListTo(cmmLeadUnitList, CmmLeadManagementUnit.class));
    }
}
