package com.a1stream.domain.custom;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.model.PartsVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.batch.PartsDeadStockItemBO;
import com.a1stream.domain.bo.master.CMM040301BO;
import com.a1stream.domain.bo.master.CMM050101BO;
import com.a1stream.domain.bo.master.CMM050102BasicInfoBO;
import com.a1stream.domain.bo.master.CMM050102PurchaseControlBO;
import com.a1stream.domain.bo.master.CMM050102SalesControlBO;
import com.a1stream.domain.bo.master.CMM060101BO;
import com.a1stream.domain.bo.master.CMQ050101BasicInfoBO;
import com.a1stream.domain.bo.master.CMQ050101DemandDetailBO;
import com.a1stream.domain.bo.master.CMQ050101InformationBO;
import com.a1stream.domain.bo.master.CMQ050101PurchaseControlBO;
import com.a1stream.domain.bo.master.CMQ050101StockInfoBO;
import com.a1stream.domain.bo.master.CMQ050801BO;
import com.a1stream.domain.bo.parts.SPM040601BO;
import com.a1stream.domain.bo.service.ProductBO;
import com.a1stream.domain.bo.unit.CMM090101BO;
import com.a1stream.domain.form.master.CMM040301Form;
import com.a1stream.domain.form.master.CMM050101Form;
import com.a1stream.domain.form.master.CMM050102Form;
import com.a1stream.domain.form.master.CMM060101Form;
import com.a1stream.domain.form.master.CMQ050101Form;
import com.a1stream.domain.form.master.CMQ050801Form;
import com.a1stream.domain.form.unit.CMM090101Form;

public interface MstProductRepositoryCustom {

    ValueListResultBO findModelList(BaseVLForm model);

    ValueListResultBO findSdModelList(BaseVLForm model);

    ValueListResultBO findPartsList(PartsVLForm model, String siteId);

    ValueListResultBO findYamahaPartsList(PartsVLForm model);

    ValueListResultBO findServiceJobList(BaseVLForm model);

    Page<CMM050101BO> findPartsInformationInquiryAndPageList(CMM050101Form model, String siteId);

    List<CMM050101BO> findPartsInformationInquiryList(CMM050101Form model, String siteId);

    List<CMM050102PurchaseControlBO> getPurchaseControlGridAddList(CMM050102Form model, String siteId);

    List<CMM050102PurchaseControlBO> getPurchaseControlGridEditList(CMM050102Form model, String siteId);

    CMM050102BasicInfoBO getBasicInfoList(CMM050102Form model, String siteId);

    CMM050102SalesControlBO getSaleControlList(CMM050102Form model, String siteId);

    List<CMM060101BO> getSvJobData(CMM060101Form model);

    List<PartsDeadStockItemBO> getAllNoneNewAndHasStockParts(String siteId,Long facilityId);

    List<PartsDeadStockItemBO> getAllJ1TotalAndAvgDemandOnParts(String siteId,Long facilityId,Set<Long> partsIds);

    CMQ050101InformationBO findPartsInformationReportList(CMQ050101Form model, String siteId);

    List<CMQ050101BasicInfoBO> findBasicInfoList(CMQ050101Form model, String siteId);

    List<CMQ050101PurchaseControlBO> findPurchaseControlList(CMQ050101Form model, String siteId);

    List<CMQ050101StockInfoBO> findStockInfoList(CMQ050101Form model, String siteId);

    List<CMQ050101DemandDetailBO> findDemandList(CMQ050101Form model, String siteId);

    List<SPM040601BO> getMstProductPriceList(List<String> partsNoList, String siteId);

    Page<CMQ050801BO> findPartsSummaryList(CMQ050801Form model, String siteId);

    List<CMQ050801BO> findPartsSummaryExportList(CMQ050801Form model, String siteId);

    List<PartsInfoBO> findPartsInfoList(List<String> partsNoList, String siteId);

    List<PartsInfoBO> findYamahaPartsInfoList(List<String> partsNoList,String siteId);

    Long getModelCategoryIdByLvOneModelId(Long modelId);

    /**
     * @param productIds
     * @return
     */
    List<PartsInfoBO> listProductInfoForAllocate(Set<Long> productIds);

    Page<CMM040301BO> getModelInfoInquiry(CMM040301Form form, String siteId);

    List<CMM090101BO> findModelPriceList(CMM090101Form form);

    List<ProductBO> findMstProductInfo(Set<String> productCds, String productClassification);
}
