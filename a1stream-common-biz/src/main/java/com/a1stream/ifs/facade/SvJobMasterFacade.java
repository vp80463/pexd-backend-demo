package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.MstBrandVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.ifs.bo.SvJobMasterBO;
import com.a1stream.ifs.service.SvJobMasterService;

import jakarta.annotation.Resource;

@Component
public class SvJobMasterFacade {

    @Resource
    private SvJobMasterService jobMstSer;

    /**
     * IX_svJobMaster
     * MdmServiceProductManagerImpl importJobMasterData
     */
    public void importJobMasterData(List<SvJobMasterBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        List<MstProductVO> ps = new ArrayList<>();

        Set<String> codes = dataList.stream().map(SvJobMasterBO::getJobCode).collect(Collectors.toSet());
        // YAMAHA Brand master
        MstBrandVO yamaha = jobMstSer.getYamahaBrand();
        Map<String, MstProductVO> prdMap = jobMstSer.getMstProductMap(codes);
        for (SvJobMasterBO mdl : dataList) {
            MstProductVO prd;
            if (prdMap.containsKey(mdl.getJobCode())) {
                prd = prdMap.get(mdl.getJobCode());
            } else {
                prd = new MstProductVO();
            }

            prepareJobMasterData(prd, mdl, yamaha);

            ps.add(prd);
        }

        jobMstSer.maintainData(ps);
    }

    private void prepareJobMasterData(MstProductVO prd, SvJobMasterBO mdl, MstBrandVO yamaha) {

        prd.setProductCd(mdl.getJobCode());
        prd.setLocalDescription(mdl.getJobDescriptionLocal());
        prd.setSalesDescription(mdl.getJobDescriptionLocal());
        prd.setEnglishDescription(mdl.getJobDescriptionEng());
        prd.setProductLevel(0);
        prd.setRegistrationDate(ComUtil.nowDate());
        String retrieve = mdl.getJobCode() + mdl.getJobDescriptionLocal();
        prd.setProductRetrieve(retrieve.replaceAll("\\s", "").toUpperCase());
        prd.setProductClassification(ProductClsType.SERVICE.getCodeDbid());
        prd.setSalLotSize(CommonConstants.BIGDECIMAL_ZERO);
        prd.setMinPurQty(CommonConstants.BIGDECIMAL_ZERO);
        prd.setMinSalQty(CommonConstants.BIGDECIMAL_ZERO);
        prd.setPurLotSize(CommonConstants.BIGDECIMAL_ZERO);
        prd.setBrandId(yamaha.getBrandId());
        prd.setBrandCd(yamaha.getBrandCd());
    }
}
