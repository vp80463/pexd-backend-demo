package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.ifs.bo.SpDiscountRateModelBO;
import com.a1stream.ifs.service.SpDiscountRateService;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Component
public class SpDiscountRateFacade {

    @Resource
    private SpDiscountRateService discountRateService;

    /**
     * Performs differential updates on discount rates for organizations based on the provided list of SPDiscountRateBO objects.
     * This method first maps dealer codes to their basic discount rates, then retrieves and maps site IDs to their corresponding
     * organization records. It updates the discount rates in these organization records if they exist in the provided list and
     * finally updates these records in the database.
     *
     * fileList A list of objects containing dealer codes and their corresponding basic discount rates.
     *                 This list is used to update the discount rates of matching organizations.
     */
    public void doDiscountRate(List<SpDiscountRateModelBO> fileList) {

        if (!fileList.isEmpty()) {

            Map<String, BigDecimal> discountRateMap = fileList.stream().collect(Collectors.toMap(SpDiscountRateModelBO::getDealerCode, SpDiscountRateModelBO::getDiscountRate));

            Set<String> dealerCdList = fileList.stream().map(SpDiscountRateModelBO::getDealerCode).collect(Collectors.toSet());
            List<CmmSiteMasterVO> cmmSiteMasterVOList = discountRateService.getCmmSiteMasterListBySite(dealerCdList);

            Set<String> organizationCdList = cmmSiteMasterVOList.stream().map(CmmSiteMasterVO::getSiteId).collect(Collectors.toSet());
            List<CmmMstOrganizationVO> organizationVOList = discountRateService.getOrganizationListByOrganizationCd(organizationCdList);
            Map<String, CmmMstOrganizationVO> organizationMap = organizationVOList.stream().collect(Collectors.toMap(CmmMstOrganizationVO::getOrganizationCd, c -> c));

            for (CmmSiteMasterVO member: cmmSiteMasterVOList) {

                if (organizationMap.containsKey(member.getSiteId())){
                    CmmMstOrganizationVO cmmMstOrganizationVO = organizationMap.get(member.getSiteId());
                    cmmMstOrganizationVO.setDiscountRate(discountRateMap.get(member.getSiteId()));
                }
            }
            discountRateService.updateMstOrganization(organizationVOList);
        }
    }
}