package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.PageImpl;

import com.a1stream.domain.bo.batch.PartsRecommendationBO;
import com.a1stream.domain.bo.parts.SPM040201BO;
import com.a1stream.domain.form.parts.SPM040201Form;

public interface ReorderGuidelineRepositoryCustom {

    PageImpl<SPM040201BO> searchProductROPROQInfo(SPM040201Form model,String siteId);

    List<PartsRecommendationBO> getReorderGuidelineByConditions(Long pointId
                                                                     , String siteId
                                                                     , String targetYear);
}
