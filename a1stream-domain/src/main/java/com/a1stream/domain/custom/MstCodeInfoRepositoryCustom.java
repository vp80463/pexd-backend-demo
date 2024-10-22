package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.BaseHelperBO;

public interface MstCodeInfoRepositoryCustom {

    List<BaseHelperBO> getJudgementStausList(String type);

    List<BaseHelperBO> getScheduleTimeList(String siteId, String facilityId, String scheduleDate);
}
