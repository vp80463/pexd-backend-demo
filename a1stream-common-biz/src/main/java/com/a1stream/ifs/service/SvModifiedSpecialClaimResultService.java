package com.a1stream.ifs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.CmmSpecialClaimSerialPro;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvModifiedSpecialClaimResultService {

    @Resource
    private CmmSpecialClaimRepository cmmSpecialClaimRepo;

    @Resource
    private CmmSpecialClaimSerialProRepository cmmSpecialClaimSerialProRepo;

    public void maintainData(List<CmmSpecialClaimSerialProVO> ps) {

        cmmSpecialClaimSerialProRepo.saveInBatch(BeanMapUtils.mapListTo(ps, CmmSpecialClaimSerialPro.class));
    }

    public Map<String, CmmSpecialClaimVO> findCmmSpecialClaimMap(Set<String> campaignNos) {

        List<CmmSpecialClaimVO> resultVO = BeanMapUtils.mapListTo(cmmSpecialClaimRepo.findByCampaignNoIn(campaignNos), CmmSpecialClaimVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmSpecialClaimVO::getCampaignNo, Function.identity()));
    }

    public Map<String, List<CmmSpecialClaimVO>> findSpecialClaimMapByBulletin(Set<String> bulletinNos) {

        List<CmmSpecialClaimVO> resultVO = BeanMapUtils.mapListTo(cmmSpecialClaimRepo.findByBulletinNoIn(bulletinNos), CmmSpecialClaimVO.class);

        return resultVO.stream().collect(Collectors.groupingBy(CmmSpecialClaimVO::getBulletinNo));
    }

    public List<CmmSpecialClaimSerialProVO> getSpecialClaimSerialProdDetail(String frameNo, Set<Long> claimIds) {

        if (claimIds.isEmpty()) { return new ArrayList<>(); }
        return BeanMapUtils.mapListTo(cmmSpecialClaimSerialProRepo.getSpecialClaimSerialProdDetail(frameNo, claimIds), CmmSpecialClaimSerialProVO.class);
    }
}
