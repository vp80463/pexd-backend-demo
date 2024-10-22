package com.a1stream.domain.custom;

import java.util.List;
import java.util.Set;

import com.a1stream.domain.bo.batch.SdPSIExportItemBO;
import com.a1stream.domain.bo.unit.SDQ011402BO;
import com.a1stream.domain.bo.unit.SVM010402TransactionHistoryBO;
import com.a1stream.domain.form.unit.SDQ011402Form;

public interface SerializedProductTranRepositoryCustom {

    List<SDQ011402BO> getStockHistoryDetail(SDQ011402Form form);

    List<SVM010402TransactionHistoryBO> getMcTransHistData(Long serializedProductId, String siteId);

    List<SdPSIExportItemBO> getSdPSIExportList(Set<String> siteIdSet, Set<Long> facilityIdSet, Set<Long> organizationIdSet, String dateFirst, String currentDate);

}
