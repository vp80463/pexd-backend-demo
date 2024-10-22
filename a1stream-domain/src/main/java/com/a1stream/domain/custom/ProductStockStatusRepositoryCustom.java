package com.a1stream.domain.custom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.a1stream.common.model.PartsVLBO;
import com.a1stream.domain.bo.parts.SPQ030301BO;
import com.a1stream.domain.bo.parts.SPQ030501BO;
import com.a1stream.domain.bo.parts.SPQ030501PrintDetailBO;
import com.a1stream.domain.bo.unit.SDQ011301BO;
import com.a1stream.domain.form.parts.SPQ030301Form;
import com.a1stream.domain.form.parts.SPQ030501Form;
import com.a1stream.domain.form.unit.SDQ011301Form;
import com.a1stream.domain.vo.ProductStockStatusVO;

public interface ProductStockStatusRepositoryCustom {

    Page<SPQ030301BO> getPartsStockListPageable(SPQ030301Form form, String siteId);

    List<PartsVLBO> findOnHandQtyList(List<Long> productIds, String siteId, Long facilityId);

    List<SPQ030501BO> getPartsOnWorkingCheckList(SPQ030501Form form, String siteId);

    void updateStockStatusQuantitySubForStockList(List<ProductStockStatusVO> stockStatusVOList, Set<Long> productStockStatusIds);

    Map<Long, Long> getProductIdStockIdMap(String siteId, Long facilityId, String productClassification, String productStockStatusType, Set<Long> productIds);

    Integer countStockStatusQuantitySubLessZero(Set<Long> productStockStatusIds);

    void updateStockStatusQuantityAddForStockList(List<ProductStockStatusVO> stockStatusVOList, Set<Long> productStockStatusIds);

    List<SPQ030501PrintDetailBO> getPartsOnWorkingCheckPrintList(SPQ030501Form form, String siteId);

    void insertFromBK(String siteId, Long facilityId, Set<String> statusTypes);

    public List<SDQ011301BO> findStockInformationInquiry(SDQ011301Form form);

}
