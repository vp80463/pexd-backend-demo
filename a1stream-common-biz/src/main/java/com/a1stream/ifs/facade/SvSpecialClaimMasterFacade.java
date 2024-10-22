package com.a1stream.ifs.facade;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.vo.CmmSpecialClaimProblemVO;
import com.a1stream.domain.vo.CmmSpecialClaimRepairVO;
import com.a1stream.domain.vo.CmmSpecialClaimStampingVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.ifs.bo.SpecialClaimConditionCodeDetailBO;
import com.a1stream.ifs.bo.SpecialClaimJobDetailBO;
import com.a1stream.ifs.bo.SpecialClaimPIDRangeDetailBO;
import com.a1stream.ifs.bo.SpecialClaimPartDetailBO;
import com.a1stream.ifs.bo.SpecialClaimRepairPatternIFItemBO;
import com.a1stream.ifs.bo.SpecialClaimSymptonCodeDetailBO;
import com.a1stream.ifs.bo.SvSpecialClaimMasterBO;
import com.a1stream.ifs.bo.SvSpecialClaimParam;
import com.a1stream.ifs.service.SvSpecialClaimService;

import jakarta.annotation.Resource;

@Component
public class SvSpecialClaimMasterFacade {

    @Resource
    private SvSpecialClaimService specClaimSer;

    private static final String SITE_666N = CommonConstants.CHAR_DEFAULT_SITE_ID;
    private static final String SYMPTOM = PJConstants.SpecialClaimProblemCategory.SYMPTOM;
    private static final String CONDITION = PJConstants.SpecialClaimProblemCategory.CONDITION;
    private static final String PART = PJConstants.ProductClsType.PART.getCodeDbid();
    private static final String SERVICE = PJConstants.ProductClsType.SERVICE.getCodeDbid();

    /**
     * IX_svSpecialClaimMaster
     * serviceOrderManager doSvSpecialClaimImport
     */
    public void importSpecialClaim(List<SvSpecialClaimMasterBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        SvSpecialClaimParam param = new SvSpecialClaimParam();

        Set<String> campaignNumbers = dataList.stream().map(SvSpecialClaimMasterBO::getCampaignNumber).collect(Collectors.toSet());
        Map<String, CmmSpecialClaimVO> specialClaimMap = specClaimSer.getSpecialClaimMap(campaignNumbers);

        for (SvSpecialClaimMasterBO data : dataList) {

            String campaignNumber = data.getCampaignNumber();
            CmmSpecialClaimVO specialClaim;
            if (!specialClaimMap.containsKey(campaignNumber)) {
                specialClaim = CmmSpecialClaimVO.create();
            } else {
                specialClaim = specialClaimMap.get(campaignNumber);
                param.getDeleteIds().add(specialClaim.getSpecialClaimId());
            }
            buildSpecialClaimVO(specialClaim, data, campaignNumber, param);
            insertCmmSpecialClaimRelated(data, specialClaim.getSpecialClaimId(), param);
        }
        specClaimSer.maintainData(param);

        // TODO DMS to CRM
    }

    private void insertCmmSpecialClaimRelated(SvSpecialClaimMasterBO data, Long specialClaimId, SvSpecialClaimParam param) {

        for (SpecialClaimSymptonCodeDetailBO sympton : data.getSymptonCodes()) {

            buildProblemVO(specialClaimId, SYMPTOM, sympton.getSymptonCode(), param);
        }

        for (SpecialClaimConditionCodeDetailBO condition : data.getConditionCodes()) {

            buildProblemVO(specialClaimId, CONDITION, condition.getConditionCode(), param);
        }

        for (SpecialClaimRepairPatternIFItemBO repairItem : data.getRepairPatterns()) {

            for (SpecialClaimPartDetailBO parts : repairItem.getParts()) {

                buildRepairVO(repairItem, specialClaimId, PART, parts.getPartNo(), parts.getPrimaryPartsFlag(), param);
            }

            for (SpecialClaimJobDetailBO job : repairItem.getJobCodes()) {

                buildRepairVO(repairItem, specialClaimId, SERVICE, job.getJobCode(), null, param);
            }
        }

        for (SpecialClaimPIDRangeDetailBO pid : data.getPidRanges()) {

            buildStampVO(pid, specialClaimId, param);
        }
    }

    private void buildStampVO(SpecialClaimPIDRangeDetailBO pid, Long specialClaimId, SvSpecialClaimParam param) {

        CmmSpecialClaimStampingVO stamp = new CmmSpecialClaimStampingVO();

        stamp.setSiteId(SITE_666N);
        stamp.setSpecialClaimId(specialClaimId);
        stamp.setStampingStyle(pid.getStampingStyle());
        stamp.setSerialNoFrom(pid.getSerialNumberFrom());
        stamp.setSerialNoTo(pid.getSerialNumberTo());

        param.getUpdStampList().add(stamp);
    }

    private void buildRepairVO(SpecialClaimRepairPatternIFItemBO repairItem, Long specialClaimId, String productCls, String productCd, String partsFlag, SvSpecialClaimParam param) {

        CmmSpecialClaimRepairVO repair = new CmmSpecialClaimRepairVO();

        repair.setSiteId(SITE_666N);
        repair.setSpecialClaimId(specialClaimId);
        repair.setRepairPattern(repairItem.getRepairPatternCode());
        repair.setRepairType(repairItem.getCampaignRepairType());
        repair.setProductClassification(productCls);
        repair.setProductCd(productCd);
        repair.setMainDamagePartsFlag(partsFlag);

        param.getUpdRepairList().add(repair);
    }

    /**
     * @param specialClaimId
     * @param sympton
     */
    private void buildProblemVO(Long specialClaimId, String category, String problemCd, SvSpecialClaimParam param) {

        CmmSpecialClaimProblemVO problem = new CmmSpecialClaimProblemVO();

        problem.setSpecialClaimId(specialClaimId);
        problem.setSiteId(SITE_666N);
        problem.setProblemCategory(category);
        problem.setProblemCd(problemCd);

        param.getUpdProblemList().add(problem);
    }

    /**
     * @param updCmmSpecialClaimVOList
     * @param data
     * @param campaignNumber
     * @param specialClaim
     */
    private void buildSpecialClaimVO(CmmSpecialClaimVO specialClaim, SvSpecialClaimMasterBO data, String campaignNumber, SvSpecialClaimParam param) {

        specialClaim.setCampaignNo(campaignNumber);
        specialClaim.setSiteId(SITE_666N);
        specialClaim.setCampaignType(data.getCampaignType());
        specialClaim.setCampaignTitle(data.getCampaignTitle());
        specialClaim.setEffectiveDate(data.getEffectiveDate());
        specialClaim.setExpiredDate(data.getExpiredDate());
        specialClaim.setBulletinNo(data.getBulletinNo());

        param.getUpdCmmSpecialClaimVOList().add(specialClaim);
    }
}
