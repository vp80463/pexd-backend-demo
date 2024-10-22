package com.a1stream.domain.custom;

import java.util.List;
import java.util.Map;

public interface PoSoItemRelationRepositoryCustom {

    List<Map> getPoQtyBySalesOrderItem(List<Long> salesOrderItemIds);
}
