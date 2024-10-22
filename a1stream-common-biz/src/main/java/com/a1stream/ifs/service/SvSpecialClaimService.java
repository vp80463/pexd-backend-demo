package com.a1stream.ifs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.CmmSpecialClaim;
import com.a1stream.domain.entity.CmmSpecialClaimProblem;
import com.a1stream.domain.entity.CmmSpecialClaimRepair;
import com.a1stream.domain.entity.CmmSpecialClaimSerialPro;
import com.a1stream.domain.entity.CmmSpecialClaimStamping;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmSpecialClaimProblemRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepairRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.repository.CmmSpecialClaimStampingRepository;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimProblemVO;
import com.a1stream.domain.vo.CmmSpecialClaimRepairVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimStampingVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.ifs.bo.SvSpecialClaimParam;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvSpecialClaimService {

    @Resource
    private CmmSpecialClaimRepository cmmSpecClaimRepo;

    @Resource
    private CmmSpecialClaimSerialProRepository cmmSpecClaimSerialProRepo;

    @Resource
    private CmmSpecialClaimRepairRepository cmmSpecClaimRepairRepo;

    @Resource
    private CmmSpecialClaimStampingRepository cmmSpecClaimStampRepo;

    @Resource
    private CmmSpecialClaimProblemRepository cmmSpecClaimProblemRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerialProductRepo;

    public void maintainData(SvSpecialClaimParam param) {

        Set<Long> deleteIds = param.getDeleteIds();
        Set<Long> delSpecClaimSerialProId = param.getDelSpecClaimSerialProId();
        List<CmmSpecialClaimVO> updCmmSpecialClaimVOList = param.getUpdCmmSpecialClaimVOList();
        List<CmmSpecialClaimProblemVO> updProblemList = param.getUpdProblemList();
        List<CmmSpecialClaimRepairVO> updRepairList = param.getUpdRepairList();
        List<CmmSpecialClaimStampingVO> updStampList = param.getUpdStampList();
        List<CmmSpecialClaimSerialProVO> updSerialProList = param.getUpdSerialProList();

        if (!delSpecClaimSerialProId.isEmpty()) {
            cmmSpecClaimSerialProRepo.removeSpecialClaimSerialProByKey(delSpecClaimSerialProId);
        }
        if (!deleteIds.isEmpty()) {
            // 移除旧的关联数据
            cmmSpecClaimRepairRepo.removeRelatedRepair(deleteIds);
            cmmSpecClaimStampRepo.removeRelatedStamp(deleteIds);
            cmmSpecClaimProblemRepo.removeRelatedProblem(deleteIds);
        }

        cmmSpecClaimRepo.saveInBatch(BeanMapUtils.mapListTo(updCmmSpecialClaimVOList, CmmSpecialClaim.class));
        cmmSpecClaimProblemRepo.saveInBatch(BeanMapUtils.mapListTo(updProblemList, CmmSpecialClaimProblem.class));
        cmmSpecClaimRepairRepo.saveInBatch(BeanMapUtils.mapListTo(updRepairList, CmmSpecialClaimRepair.class));
        cmmSpecClaimStampRepo.saveInBatch(BeanMapUtils.mapListTo(updStampList, CmmSpecialClaimStamping.class));
        cmmSpecClaimSerialProRepo.saveInBatch(BeanMapUtils.mapListTo(updSerialProList, CmmSpecialClaimSerialPro.class));
    }

    public Map<String, CmmSerializedProductVO> getSerialProductMap(Set<String> frameNos) {

        List<CmmSerializedProduct> result = cmmSerialProductRepo.findByFrameNoIn(frameNos);
        List<CmmSerializedProductVO> resultVO = BeanMapUtils.mapListTo(result, CmmSerializedProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmSerializedProductVO::getFrameNo, Function.identity()));
    }

    public Map<String, CmmSpecialClaimVO> getSpecialClaimMap(Set<String> campaignNos) {

        List<CmmSpecialClaim> result = cmmSpecClaimRepo.findByCampaignNoIn(campaignNos);
        List<CmmSpecialClaimVO> resultVO = BeanMapUtils.mapListTo(result, CmmSpecialClaimVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmSpecialClaimVO::getCampaignNo, Function.identity()));
    }

    public Map<String, List<CmmSpecialClaimVO>> getSpecialClaimByBulletin(Set<String> bulletinNos) {

        List<CmmSpecialClaim> result = cmmSpecClaimRepo.findByBulletinNoIn(bulletinNos);
        List<CmmSpecialClaimVO> resultVO = BeanMapUtils.mapListTo(result, CmmSpecialClaimVO.class);

        return resultVO.stream().collect(Collectors.groupingBy(CmmSpecialClaimVO::getBulletinNo));
    }

    public List<CmmSpecialClaimSerialProVO> findSpecialClaimSerilProDetails(Set<Long> claimIds) {

        List<CmmSpecialClaimSerialPro> result = cmmSpecClaimSerialProRepo.findSpecClaimSerialProdDtl(claimIds);
        List<CmmSpecialClaimSerialProVO> resultVO = BeanMapUtils.mapListTo(result, CmmSpecialClaimSerialProVO.class);

        return resultVO;
    }
}
