package com.a1stream.domain.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.parts.SPM030901BO;
import com.a1stream.domain.bo.parts.SPM031001BO;
import com.a1stream.domain.bo.parts.SPQ030601BO;
import com.a1stream.domain.bo.parts.SPQ030701BO;
import com.a1stream.domain.form.parts.SPM030901Form;
import com.a1stream.domain.form.parts.SPQ030601Form;
import com.a1stream.domain.form.parts.SPQ030701Form;

public interface ProductStockTakingRepositoryCustom {

    Page<SPQ030601BO> getPartsStocktakingListPageable(SPQ030601Form form, String siteId);

    Page<SPQ030701BO> getPartsStocktakingProgressList(SPQ030701Form form, String siteId);

    Map<String,SPM031001BO> getProductStockTakingByType(String siteId, Long facilityId, String productClassification);

    List<SPM031001BO> getStockTakingSummary(String siteId, Long facilityId);

    List<SPM031001BO> getProductStockTakingList(String siteId, Long facilityId);

    /**
     * anthor: Tang Tiantian
     */
    List<SPM030901BO> listProductStockTaking(SPM030901Form form);

    Map<String,SPM031001BO> getPrintPartsStocktakingResultList(Long facilityId, String siteId);

    public List<SPQ030701BO> getPrintPartsStocktakingProgressList(SPQ030701Form form, String siteId);

    public List<SPM031001BO> getPartsStocktakingResultStatisticsList(String siteId, Long facilityId);

    public List<SPM031001BO> getPrintPartsStocktakingGapList(String siteId, Long facilityId);

    public List<SPM031001BO> getPrintPartsStocktakingLedgerList(String siteId, Long facilityId);

    public List<SPQ030601BO> getPrintPartsStocktakingResultList(SPQ030601Form form, String siteId);
}
