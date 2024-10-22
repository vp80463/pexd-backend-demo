package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.batch.PartsRecommendationBO;


public interface PurchaseRecommendationRepositoryCustom {

    public List<PartsRecommendationBO> getPurchaseRecommendationList(Long pointId
                                                                    , Long supplierId
                                                                    , String siteId);
}
