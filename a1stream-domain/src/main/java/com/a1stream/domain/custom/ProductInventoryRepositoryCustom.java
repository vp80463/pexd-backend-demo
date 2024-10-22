package com.a1stream.domain.custom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.a1stream.common.model.LocationVLBO;
import com.a1stream.common.model.LocationVLForm;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.domain.bo.parts.SPM030101BO;
import com.a1stream.domain.bo.parts.SPM030501BO;
import com.a1stream.domain.bo.parts.SPM030801BO;
import com.a1stream.domain.bo.parts.SPQ030302BO;
import com.a1stream.domain.bo.parts.SPQ030801BO;
import com.a1stream.domain.form.parts.SPM030501Form;
import com.a1stream.domain.form.parts.SPQ030302Form;
import com.a1stream.domain.vo.ProductInventoryVO;

public interface ProductInventoryRepositoryCustom {
    Page<LocationVLBO> findLocationList(LocationVLForm model,String siteId);

    List<PartsVLBO> findMainLocationIdList(List<Long> productIds, String siteId, Long facilityId);

    List<SPM030501BO> getPartsLocationList(SPM030501Form form, String siteId);

    List<SPM030801BO> getStockOnlyLocationInfo(String siteId, Long facilityId, String costType, String productClassification);

    List<SPM030801BO> getAllLocationInfo(String siteId, Long facilityId, String productClassification);

    Page<SPQ030302BO> getPartsStockDetailListPageable(SPQ030302Form form, String siteId);

    List<SPM030101BO> getProductInventoryList(String siteId, Long facilityId, Set<Long> productIdSet, String productClassification, String primaryFlag,String locationType);

    Map<String, ProductInventoryVO> getProInvByCds(Set<String> locationCds, Set<String> productCds, Long facilityId, String siteId);

    List<ProductInventoryVO> getProInventoryToDel(String siteId, Long facilityId);

    Map<String, SPQ030801BO> getLocationInUse(String siteId, Long pointId, Long binTypeId, String locationTypeId);
}
