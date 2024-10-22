package com.a1stream.ifs.facade;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.ifs.bo.SvSpecialClaimApplicationBO;
import com.a1stream.ifs.bo.SvSpecialClaimParam;
import com.a1stream.ifs.service.SvSpecialClaimService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SvSpecialClaimApplicationFacade {

    @Resource
    private SvSpecialClaimService specClaimSer;

    private static final String Y = CommonConstants.CHAR_Y;

    /**
     * IX_svSpecialClaimApplication
     * ISvESBServiceManager serviceOrderManager doSvSpecialClaimApplicationImport
     */
    public void importSpecialClaimApplication(List<SvSpecialClaimApplicationBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        SvSpecialClaimParam param = new SvSpecialClaimParam();

        Set<String> campaignNumbers = dataList.stream().map(SvSpecialClaimApplicationBO::getCampaignNumber).collect(Collectors.toSet());
        Map<String, CmmSpecialClaimVO> specialClaimMap = specClaimSer.getSpecialClaimMap(campaignNumbers);

        Set<String> bulletinNos = specialClaimMap.values().stream().map(CmmSpecialClaimVO::getBulletinNo).collect(Collectors.toSet());
        Map<String, List<CmmSpecialClaimVO>> specClaimByBulletinMap = specClaimSer.getSpecialClaimByBulletin(bulletinNos);

        List<CmmSpecialClaimVO> allSpecClaimByBulletin = specClaimByBulletinMap.values().stream().filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());

        Set<Long> specialClaimIds = allSpecClaimByBulletin.stream().map(CmmSpecialClaimVO::getSpecialClaimId).collect(Collectors.toSet());
        List<CmmSpecialClaimSerialProVO> specialClaimSerilProDetails = specClaimSer.findSpecialClaimSerilProDetails(specialClaimIds);

        for (SvSpecialClaimApplicationBO item : dataList) {

            String campaignNumber = item.getCampaignNumber();
            if (specialClaimMap.containsKey(campaignNumber)) {

                CmmSpecialClaimVO cmmSpecialClaim = specialClaimMap.get(campaignNumber);
                String bulletinNo = cmmSpecialClaim.getBulletinNo()== null ? CommonConstants.CHAR_BLANK : cmmSpecialClaim.getBulletinNo();
                if (specClaimByBulletinMap.containsKey(bulletinNo)) {

                    List<CmmSpecialClaimVO> specClaimList = specClaimByBulletinMap.get(bulletinNo);
                    Set<Long> specialClaimId = specClaimList.stream().map(CmmSpecialClaimVO::getSpecialClaimId).collect(Collectors.toSet());

                    List<CmmSpecialClaimSerialProVO> cmmSpecialClaimSerialProDetails = specialClaimSerilProDetails.stream()
                                .filter(dbProItem -> (StringUtils.equals(item.getFrameNo(), dbProItem.getFrameNo())
                                                && specialClaimId.contains(dbProItem.getSpecialClaimId())
                                                )).collect(Collectors.toList());

                    for (CmmSpecialClaimSerialProVO proDetail : cmmSpecialClaimSerialProDetails) {

                        modifySpecialClaimSerialProVO(item, proDetail, param);
                    }
                }
            }
        }

        specClaimSer.maintainData(param);
    }

    /**
     * @param updCmmSpecClaimSerialProList
     * @param item
     * @param cmmSpecialClaimSerialProDetail
     */
    private void modifySpecialClaimSerialProVO(SvSpecialClaimApplicationBO item, CmmSpecialClaimSerialProVO proDetail, SvSpecialClaimParam param) {

        proDetail.setClaimFlag(Y);
        proDetail.setDealerCd(item.getApplicationDealerCode());
        proDetail.setFacilityCd(item.getApplicationPointCode());
        proDetail.setApplyDate(item.getApplicationDate());

        param.getUpdSerialProList().add(proDetail);
    }
}
