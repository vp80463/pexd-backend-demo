package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ProductTaxRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class PartsManager {

    @Resource
    MstProductRepository mstProductRepository;

    @Resource
    MstProductRelationRepository mstProductRelationRepository;

    @Resource
    ProductInventoryRepository productInventoryRepository;

    @Resource
    ProductStockStatusRepository productStockStatusRepository;

    @Resource
    ProductTaxRepository productTaxRepository;

    @Resource
    MstCodeInfoRepository mstCodeInfoRepository;

    @Resource
    SystemParameterRepository systemParameterRepository;

    // 获取和partsValueList同样的结果 仿照ValueListFacade中的写法
    public List<PartsInfoBO> getPartsInfoList(List<String> partsNoList, String siteId, Long facilityId, String taxRateFromDate) {

        List<PartsInfoBO> list = findPartsInfoList(partsNoList, siteId);
        Map<Long, PartsInfoBO> map = setValue2PartsBO(list, facilityId, taxRateFromDate, false);
        
        return map.values().stream().toList();
    }

    public List<PartsInfoBO> getYamahaPartsInfoList(List<String> partsNoList, Long facilityId, String taxRateFromDate,String ucSiteId) {

        List<PartsInfoBO> list = findYamahaPartsInfoList(partsNoList,ucSiteId);
        
        Map<Long, PartsInfoBO> map = setValue2PartsBO(list, facilityId, taxRateFromDate, true);
        
        return map.values().stream().toList();
    }

    public List<PartsInfoBO> findPartsInfoList(List<String> partsNoList, String siteId) {
        return mstProductRepository.findPartsInfoList(partsNoList, siteId);
    }

    public List<PartsInfoBO> findYamahaPartsInfoList(List<String> partsNoList,String siteId) {
        return mstProductRepository.findYamahaPartsInfoList(partsNoList,siteId);
    }

    public List<PartsInfoBO> findSupersedingPartsIdList(List<Long> productIds) {
        return BeanMapUtils.mapListTo(mstProductRelationRepository.findSupersedingPartsIdList(productIds), PartsInfoBO.class);
    }

    public List<PartsInfoBO> findMainLocationIdList(List<Long> productIds,String siteId, Long facilityId, boolean yamahaFlag) {
    	siteId = yamahaFlag? CommonConstants.CHAR_DEFAULT_SITE_ID : siteId;
        return BeanMapUtils.mapListTo(productInventoryRepository.findMainLocationIdList(productIds, siteId, facilityId), PartsInfoBO.class);
    }

    public List<PartsInfoBO> findOnHandQtyList(List<Long> partsIdList, String siteId, Long facilityId, boolean yamahaFlag) {
    	siteId = yamahaFlag? CommonConstants.CHAR_DEFAULT_SITE_ID : siteId;
        return BeanMapUtils.mapListTo(productStockStatusRepository.findOnHandQtyList(partsIdList, siteId, facilityId), PartsInfoBO.class);
    }

    public List<PartsInfoBO> findProductTaxList(List<Long> productIds) {
        return BeanMapUtils.mapListTo(productTaxRepository.findProductTaxList(productIds), PartsInfoBO.class);
    }

    private BigDecimal getDefaultOrJobTaxRate(String taxRateType) {
        SystemParameterVO taxInfo = BeanMapUtils.mapTo(systemParameterRepository.findBySystemParameterTypeId(taxRateType), SystemParameterVO.class);
        return taxInfo == null ? BigDecimal.TEN : new BigDecimal(taxInfo.getParameterValue());
    }

	private Map<Long, PartsInfoBO> setValue2PartsBO(List<PartsInfoBO> list, Long facilityId, String taxRateFromDate, boolean yamahaFlag) {
		
		if (list.isEmpty()) {
			return new HashMap<Long, PartsInfoBO>();
		}
		BigDecimal defaultTaxRate = getDefaultOrJobTaxRate(MstCodeConstants.SystemParameterType.TAXRATE);
        for (PartsInfoBO part : list) {
            part.setCodeFmt(PartNoUtil.format(part.getPartsNo()));
            part.setTaxRate(defaultTaxRate);
            part.setOnHandQty(BigDecimal.ZERO);
            if (StringUtils.isNotBlank(part.getDesc()) && part.getDesc().contains(CommonConstants.CHAR_SPACE)) {
                String[] desc = part.getDesc().split(CommonConstants.CHAR_SPACE, CommonConstants.INTEGER_TWO);
                part.setDesc(PartNoUtil.format(desc[0]) + CommonConstants.CHAR_SPACE + desc[1]);
            }
        }
        List<Long> partsIdList = list.stream().map(PartsInfoBO::getPartsId).toList();
        List<PartsInfoBO> supersedingPartsIdList = this.findSupersedingPartsIdList(partsIdList);
        List<PartsInfoBO> mainLocationIdList = this.findMainLocationIdList(partsIdList, null, facilityId, yamahaFlag);
        List<PartsInfoBO> onHandQtyList = this.findOnHandQtyList(partsIdList, null, facilityId, yamahaFlag);
        List<PartsInfoBO> productTaxList= this.findProductTaxList(partsIdList);

        Map<Long, PartsInfoBO> map = list.stream().collect(Collectors.toMap(PartsInfoBO::getPartsId, partsInfoBO -> partsInfoBO));
        supersedingPartsIdList.forEach(part -> {
            PartsInfoBO partsInfoBO = map.get(part.getId());
            if (!Nulls.isNull(partsInfoBO)) {
                partsInfoBO.setSupersedingPartsId(part.getSupersedingPartsId());
                partsInfoBO.setSupersedingPartsCd(part.getSupersedingPartsCd());
                partsInfoBO.setSupersedingPartsNm(part.getSupersedingPartsNm());
            }
        });
        mainLocationIdList.forEach(part -> {
            PartsInfoBO partsInfoBO = map.get(part.getId());
            if (!Nulls.isNull(partsInfoBO)) {
                partsInfoBO.setMainLocationId(part.getMainLocationId());
                partsInfoBO.setMainLocationCd(part.getMainLocationCd());
            }
        });
        onHandQtyList.forEach(part -> {
            PartsInfoBO partsInfoBO = map.get(part.getId());
            if (!Nulls.isNull(partsInfoBO)) {
                partsInfoBO.setOnHandQty(part.getOnHandQty());
            }
        });
        productTaxList.forEach(part -> {
            PartsInfoBO partsInfoBO = map.get(part.getId());
            if (StringUtils.isNotBlank(taxRateFromDate) && DateUtils.isAfterOrEqualDate(DateUtils.getCurrentDate(), DateUtils.string2Date(taxRateFromDate, DateUtils.FORMAT_YMD_NODELIMITER)) && !Nulls.isNull(partsInfoBO)) {
                partsInfoBO.setTaxRate(part.getTaxRate());
            }
        });
        
		return map;
	}
}