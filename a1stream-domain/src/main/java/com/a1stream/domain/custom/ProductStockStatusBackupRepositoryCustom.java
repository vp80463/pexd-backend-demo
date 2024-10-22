package com.a1stream.domain.custom;

import java.util.Set;

public interface ProductStockStatusBackupRepositoryCustom {

    void insertCopy(String siteId, Long facilityId, Set<String> statusTypes);
}
