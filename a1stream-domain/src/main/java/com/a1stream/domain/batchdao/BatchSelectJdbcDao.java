package com.a1stream.domain.batchdao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.a1stream.domain.bo.batch.BSProductStockStatusBO;
import com.a1stream.domain.bo.batch.CuAbcDefinitionInfoBatchBO;
import com.a1stream.domain.bo.batch.PartsBatchBO;
import com.a1stream.domain.bo.batch.ProductAbcBatchBO;
import com.a1stream.domain.bo.batch.ProductOrderResultSummaryBatchBO;
import com.a1stream.domain.bo.batch.RrDemandForecastMonthItemBatchBO;
import com.a1stream.domain.bo.batch.SiSeasonIndexInfoModelBO;
import com.a1stream.domain.bo.batch.SsmProductAbcBatchBO;

public interface BatchSelectJdbcDao {

    public List<String> getCmmSiteList(String apServerUrl);
    public String getSystemParDate(String siteId
                                 , String sysParamTypeId);
    public String getAcctMonthInfo(String siteId
                                 , String accountMonth);
    public List<Long> getFacilityList(String siteId,String collectDate);
    public List<PartsBatchBO> getAllProductId(String siteId);
    public Map<Long, Long> getReplaceProductIdMap(String siteId) ;
    public Map<String, String> getPartsRopqParameter(String siteId);
    public Map<String, ProductOrderResultSummaryBatchBO> getProductOrderResultSummaryByTargetYear(String siteId,Long facilityId,String targetYear);
    public Map<Long, List<Long>> getReplaceToProductIdMap(String siteId);
    public  List<CuAbcDefinitionInfoBatchBO> getAbcDefinition(String siteId);
    public List<PartsBatchBO> getProductsGroupbyFacility(String siteId);
    public Map<Long,String> getProductCost(String siteId);
    public List<ProductAbcBatchBO> getProductProductAbcTypeABCN(String siteId,Long facilityId);
    public Map<String, String> getPartsRopqMonthlyMapString(String siteId,Long facilityId, String typeId);
    public Map<String, SsmProductAbcBatchBO> getReorderGuideline(String siteId,Long facilityId);
    public Map<String, SsmProductAbcBatchBO> getReorderGuidelineFlagIsOne(String siteId,Long facilityId);
    public Map<String, SsmProductAbcBatchBO> getPartsRopqMonthlyMap(String siteId,Long facilityId,String typeId);
    public Map<String, String> getTrendIndexUpperAndLowerValues(String siteId);
    public List<String> getSeasonIndex(String siteId,Long facilityId);
    public List<PartsBatchBO> getProductABCCategory(String siteId,Long facilityId);
    public List<ProductAbcBatchBO> getAbcType(String siteId,Long facilityId);
    public void searchFacilityPartsAvgMonthAndTrendIdx(String siteId, final Map<String, String> facPrdAvgMonthMap,final Map<String, String> facPrdTrendIdxMap);
    public Map<String, SiSeasonIndexInfoModelBO> getSeasonIndexInfoMap(String siteId,Long facilityId);
    public Map<String, SiSeasonIndexInfoModelBO> getSeasonIndexManualInfoMap(String siteId,Long facilityId);
    public List<RrDemandForecastMonthItemBatchBO> getDemandForecast(String siteId,Long facilityId);
    public Set<String> getReorderGuidelineMap(String siteId,Long facilityId);
    public Set<String> getProductTypeDEXY(String siteId,Long facilityId);
    public List<BSProductStockStatusBO> getProductStockStatus(String siteId
                                                             ,String costType
                                                             ,String classificationId
                                                             ,Long facilityId
                                                             ,List<String> typeIds);
    public List<BSProductStockStatusBO> getSerializdeProduct(String siteId
                                                            ,Long facilityId
                                                            ,List<Long> serializedProductIdS);

    public List<Long> getSerializedProductId(String siteId
                                            ,List<String> statusList);

    public List<ProductOrderResultSummaryBatchBO> getSalesResultProductListSP(String siteId
                                                                             ,String collectDate);

    public List<ProductOrderResultSummaryBatchBO> getSalesResultProductListSV(String siteId
                                                                              ,String collectDate);
    public Map<String, Long> getProductOrderResultSummary(String siteId, String targetYear);
}
