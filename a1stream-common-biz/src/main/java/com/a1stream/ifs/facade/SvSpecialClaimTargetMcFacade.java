package com.a1stream.ifs.facade;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.ifs.bo.SpecialClaimFrameNoDetailBO;
import com.a1stream.ifs.bo.SvSpecialClaimParam;
import com.a1stream.ifs.bo.SvSpecialClaimTargetMcBO;
import com.a1stream.ifs.service.SvSpecialClaimService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SvSpecialClaimTargetMcFacade {

    @Resource
    private SvSpecialClaimService specClaimSer;

    private static final String N = CommonConstants.CHAR_N;

    /**
     * IX_svSpecialClaimTargetMc
     * SvESBServiceManagerImpl serviceOrderManager doSvSpecialClaimTargetMcImport
     */
    public void importSpecialClaimTargetMc(List<SvSpecialClaimTargetMcBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        SvSpecialClaimParam param = new SvSpecialClaimParam();

        Set<String> campaignNumbers = dataList.stream().map(SvSpecialClaimTargetMcBO::getCampaignNumber).collect(Collectors.toSet());
        Map<String, CmmSpecialClaimVO> specialClaimMap = specClaimSer.getSpecialClaimMap(campaignNumbers);

        Set<Long> specialClaimIds = specialClaimMap.values().stream().map(CmmSpecialClaimVO::getSpecialClaimId).collect(Collectors.toSet());
        List<CmmSpecialClaimSerialProVO> specialClaimSerilProDetails = specClaimSer.findSpecialClaimSerilProDetails(specialClaimIds);

        Set<String> frameNos = specialClaimSerilProDetails.stream().map(CmmSpecialClaimSerialProVO::getFrameNo).collect(Collectors.toSet());
        Map<String, CmmSerializedProductVO> serialProductMap = specClaimSer.getSerialProductMap(frameNos);

        for (SvSpecialClaimTargetMcBO data : dataList) {

            String campaignNumber = data.getCampaignNumber();
            if (specialClaimMap.containsKey(campaignNumber)) {
                CmmSpecialClaimVO cmmSpecialClaim = specialClaimMap.get(campaignNumber);

                List<CmmSpecialClaimSerialProVO> details = specialClaimSerilProDetails.stream()
                        .filter(item -> (StringUtils.equals(item.getRepairPattern(), data.getRepairPatternCode())
                                        && StringUtils.equals(item.getClaimFlag(), N)
                                        && item.getSpecialClaimId() == cmmSpecialClaim.getSpecialClaimId()))
                        .collect(Collectors.toList());

                if (!details.isEmpty()) {

                    Set<Long> delSpecClaimSerialProId = details.stream().map(CmmSpecialClaimSerialProVO::getSpecialClaimSerialProId).collect(Collectors.toSet());

                    param.getDelSpecClaimSerialProId().addAll(delSpecClaimSerialProId);
                }

                for (SpecialClaimFrameNoDetailBO frameData : data.getFrameNos()) {
                    String frameNo = frameData.getFrameNo();
                    if (!frameNos.contains(frameNo)) {
                        if (serialProductMap.containsKey(frameNo)) {

                            buildSpecialClaimSerialProVO(data, cmmSpecialClaim, frameNo, param);
                        }

                    }
                }
            }
        }

        specClaimSer.maintainData(param);

        // TODO Add to queue
        if (!param.getUpdSerialProList().isEmpty()) {

        }
    }

    /**
     * @param data
     * @param cmmSpecialClaim
     * @param frameNo
     * @return
     */
    private void buildSpecialClaimSerialProVO(SvSpecialClaimTargetMcBO data, CmmSpecialClaimVO cmmSpecialClaim, String frameNo, SvSpecialClaimParam param) {

        CmmSpecialClaimSerialProVO cmmSpecialClaimSerialProDetailNew = new CmmSpecialClaimSerialProVO();

        cmmSpecialClaimSerialProDetailNew.setSpecialClaimId(cmmSpecialClaim.getSpecialClaimId());
        cmmSpecialClaimSerialProDetailNew.setRepairPattern(data.getRepairPatternCode());
        cmmSpecialClaimSerialProDetailNew.setFrameNo(frameNo);
        cmmSpecialClaimSerialProDetailNew.setClaimFlag(N);

        param.getUpdSerialProList().add(cmmSpecialClaimSerialProDetailNew);
    }
}
