package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM010201HistoryBO;

public interface ServiceOrderEditHistoryRepositoryCustom {

    List<SVM010201HistoryBO> listServiceEditHistoryByOrderId(Long serviceOrderId);
}
