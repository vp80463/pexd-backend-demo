package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.ifs.bo.SvModifiedSpecialClaimResultBO;
import com.a1stream.ifs.service.IfsCommService;
import com.a1stream.ifs.service.SvModifiedSpecialClaimResultService;

import jakarta.annotation.Resource;

@Component
public class SvModifiedSpecialClaimResultFacade {

    @Resource
    private SvModifiedSpecialClaimResultService modSpecClaimRstSer;

    @Resource
    private IfsCommService cmmSiteMstSer;

    /**
     * IX_svModifiedSpecialClaimResult
     * ISvESBServiceManager serviceOrderManager doSvModifiedSpecialClaimResultImport
     */
    public void importModifiedSpecialClaimResult(List<SvModifiedSpecialClaimResultBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        List<CmmSpecialClaimSerialProVO> updSpecialClaimSerPros = new ArrayList<>();
        // 启用的经销商列表
        Set<String> dealerCds = dataList.stream().map(SvModifiedSpecialClaimResultBO::getApplicationDealerCode).collect(Collectors.toSet());
        Map<String, CmmSiteMasterVO> activeDealerMap = cmmSiteMstSer.getActiveDealerMap(dealerCds);

        // Extracting dealer code as the key
        Function<SvModifiedSpecialClaimResultBO, String> keyExtractor = SvModifiedSpecialClaimResultBO::getApplicationDealerCode;
        Map<String, List<SvModifiedSpecialClaimResultBO>> siteDataMap = cmmSiteMstSer.groupBySite(dataList, keyExtractor, activeDealerMap);

        Set<String> campaignNos = dataList.stream().map(SvModifiedSpecialClaimResultBO::getCampaignNumber).collect(Collectors.toSet());
        Map<String, CmmSpecialClaimVO> campaignMap = modSpecClaimRstSer.findCmmSpecialClaimMap(campaignNos);

        Set<String> bulletinNos = campaignMap.values().stream().map(CmmSpecialClaimVO::getBulletinNo).collect(Collectors.toSet());
        Map<String, List<CmmSpecialClaimVO>> bulletinMap = modSpecClaimRstSer.findSpecialClaimMapByBulletin(bulletinNos);

        for(String siteId : activeDealerMap.keySet()) {

            List<SvModifiedSpecialClaimResultBO> items = siteDataMap.get(siteId);
            for (SvModifiedSpecialClaimResultBO item : items) {
                if (campaignMap.containsKey(item.getCampaignNumber())) {

                    CmmSpecialClaimVO cmmSpecialClaim = campaignMap.get(item.getCampaignNumber());
                    String bulletinNo = cmmSpecialClaim.getBulletinNo()== null ? CommonConstants.CHAR_BLANK : cmmSpecialClaim.getBulletinNo();

                    Set<Long> specialClaims1 = new HashSet<>();
                    if (bulletinMap.containsKey(bulletinNo)) {
                        List<CmmSpecialClaimVO> cmmSpecialClaimList = bulletinMap.get(bulletinNo);
                        specialClaims1 = cmmSpecialClaimList.stream().map(CmmSpecialClaimVO::getSpecialClaimId).collect(Collectors.toSet());
                    }

                    String applicationDate = null;
                    String beforeFrameNo = item.getBeforeFrameNo().isEmpty()? CommonConstants.CHAR_SPACE : item.getBeforeFrameNo();
                    String afterFrameNo = item.getAfterFrameNo().isEmpty()? CommonConstants.CHAR_SPACE : item.getAfterFrameNo();

                    List<CmmSpecialClaimSerialProVO> beforeSerialProds = modSpecClaimRstSer.getSpecialClaimSerialProdDetail(beforeFrameNo, specialClaims1);
                    List<CmmSpecialClaimSerialProVO> afterSerialProds = modSpecClaimRstSer.getSpecialClaimSerialProdDetail(afterFrameNo, specialClaims1);

                    for (CmmSpecialClaimSerialProVO detail : beforeSerialProds) {
                        applicationDate = detail.getApplyDate();
                        detail.setClaimFlag(CommonConstants.CHAR_N);
                        detail.setDealerCd(null);
                        detail.setFacilityCd(null);
                        detail.setApplyDate(null);

                        updSpecialClaimSerPros.add(detail);
                    }
                    for (CmmSpecialClaimSerialProVO detail : afterSerialProds) {
                        detail.setClaimFlag(CommonConstants.CHAR_Y);
                        detail.setDealerCd(item.getApplicationDealerCode());
                        detail.setFacilityCd(item.getApplicationPointCode());
                        detail.setApplyDate(applicationDate);

                        updSpecialClaimSerPros.add(detail);
                    }

                    // TODO insert QueueApiData table forspecial_claim
                }
            }
        }

        modSpecClaimRstSer.maintainData(updSpecialClaimSerPros);
    }
}
