package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM010201ServiceHistoryBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceHistoryBO;
import com.a1stream.domain.bo.unit.SVM010402ServiceHistoryBO;

public interface CmmServiceHistoryRepositoryCustom {

    Integer getMaxBaseDateByMcId(Long serializedProductId);

    List<SVM010201ServiceHistoryBO> findServiceHistoryByMotorId(Long serializedProductId, String siteId);

    List<SVM0102PrintServiceHistoryBO> getServiceHistoryPrintList(Long serviceOrderId, String siteId);

    List<SVM010402ServiceHistoryBO> getMcServiceHistData(Long cmmSerializedProductId);

}
