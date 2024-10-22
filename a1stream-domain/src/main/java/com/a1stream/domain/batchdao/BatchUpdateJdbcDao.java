package com.a1stream.domain.batchdao;

import java.util.List;


public interface BatchUpdateJdbcDao {

    public void insertPartsRopqMonthlyData(List<Object[]> batchArgs);

    public void insertDemandForecastTempData(List<Object[]> batchArgs);

    public void insertProductAbc (List<Object[]> batchArgs);

    public void deleteSeasonIndexInfo(String siteId);

    public void deleteSeasonIndexInfoForFacility(String siteId,Long facilityId);

    public void insertSeasonIndexInfo (List<Object[]> batchArgs);

    public void insertDemandForecast(List<Object[]> batchArgs);

    public void insertReorderGuideLineInfo (List<Object[]> batchArgs);

    public void deleteTableDemandforecast(String siteId);

    public void deleteTableDemandforecastFacility(String siteId,Long facilityId);

    public void deleteTablePartsRopqMonthly(String siteId);

    public void deleteTableProductAbc(String siteId) ;

    public void deleteTableReorderGuideline(String siteId) ;

    public void deletePartsRopqMonthlyData(String siteId, String productFeatureCategoryId);

    public void deletePartsRopqMonthlyDataAndFacility(String siteId, Long facility, String productFeatureCategoryId );

    public void deleteTempProductFacilityFeatureData(String siteId);

    public int setSpecParameterValue(String siteId, String sysParamTypeId, String parameterValue);

    public void deleteTableReorderGuidelineFacility(String siteId,Long facilityId);

    public void updateNewPointSign(String siteId, Long facilityId);

    public void updateSiteMst(String siteId);

    public void insertSpecParameterValue(String siteId
                                        , Long systemParameterId
                                        , String sysParamTypeId
                                        , String parameterValue
                                        , String updateProgram);

    public void deleteTableProductAbcByFacility(String siteId,Long facilityId);

    public void insertInvTransaction (List<Object[]> batchArgs);

    public void updatePartsRopqParameter(List<Object[]> batchArgs);
    public void insertPartsRopqParameter(List<Object[]> batchArgs);

    public void insertProductOrderResultSummaryData(String collectMonth, List<Object[]> batchArgs);

    public void updateProductOrderResultSummaryData(String collectMonth, List<Object[]> batchArgs);
}