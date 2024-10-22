package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.unit.SVM010603BO;

public interface CmmLeadUpdateHistoryRepositoryCustom {

    List<SVM010603BO> getCmmLeadUpdHistory(Long leadResultId);
}
