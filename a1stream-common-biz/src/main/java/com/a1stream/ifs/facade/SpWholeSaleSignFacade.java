package com.a1stream.ifs.facade;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.ifs.bo.SpWholeSaleSignModelBO;
import com.a1stream.ifs.service.SpWholeSaleSignService;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Component
public class SpWholeSaleSignFacade {

    @Resource
    private SpWholeSaleSignService spWholeSaleSignService;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    public void doWholeSaleSign(List<SpWholeSaleSignModelBO> spWholeSaleSignModelBOList) {

        //文件中的dealerCode若不存在于cmm_site_master表中，则剔除出去-> update by lijiajun
        Set<String> dealerCds = spWholeSaleSignModelBOList.stream().map(o -> o.getDealerCode()).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasters = BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteCdInAndActiveFlag(dealerCds,CommonConstants.CHAR_Y),CmmSiteMasterVO.class);
        Set<String> siteIds = cmmSiteMasters.stream().map(o -> o.getSiteId()).collect(Collectors.toSet());
        spWholeSaleSignModelBOList = spWholeSaleSignModelBOList.stream()
                                                               .filter(model -> siteIds.contains(model.getDealerCode()))
                                                               .collect(Collectors.toList());
        
        Set<String> dealerCodeSet = spWholeSaleSignModelBOList.stream().map(SpWholeSaleSignModelBO::getDealerCode).collect(Collectors.toSet());
        Set<String> consigneeSet = spWholeSaleSignModelBOList.stream().map(SpWholeSaleSignModelBO::getConsigneeCode).collect(Collectors.toSet());
        
        Map<String, String> spWholeSaleSignModelBOMap = spWholeSaleSignModelBOList.stream().collect(Collectors.toMap(item -> item.getDealerCode() + item.getConsigneeCode(), item -> item.getStatus()));
        List<MstFacilityVO> mstFacilityVOList = spWholeSaleSignService.getMstFacilityVOList(dealerCodeSet, consigneeSet);
        for (MstFacilityVO vo : mstFacilityVOList) {
            String status = spWholeSaleSignModelBOMap.get(vo.getSiteId() + vo.getFacilityCd());
            if (!CommonConstants.CHAR_Y.equals(status) && !CommonConstants.CHAR_N.equals(status)) {
                throw new BusinessCodedException("Invalid status");
            }
            vo.setSpPurchaseFlag(status);
        }

        spWholeSaleSignService.doWholeSaleSign(mstFacilityVOList);
    }
}