package com.a1stream.domain.custom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageImpl;

import com.a1stream.common.model.LocationVLBO;
import com.a1stream.common.model.LocationVLForm;
import com.a1stream.domain.bo.master.CMM020101BO;
import com.a1stream.domain.bo.parts.SPQ030801BO;
import com.a1stream.domain.form.master.CMM020101Form;

public interface LocationRepositoryCustom {

    PageImpl<LocationVLBO> findLocationList(LocationVLForm model,String siteId,List<String> locationTypes);

    /**
     * author: Tang Tiantian
     */
    PageImpl<CMM020101BO> findLocInfoInquiryList(CMM020101Form model, String siteId);

    List<SPQ030801BO> getUsageLocationAll(String siteId, Long pointId, Long binTypeId, String locationTypeId);

    Map<Long, String> getlocTypeByIds(Set<Long> locationIds);
}
