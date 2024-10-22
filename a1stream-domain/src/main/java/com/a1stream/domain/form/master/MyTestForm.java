package com.a1stream.domain.form.master;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MyTestForm {

    private List<Long> selectedReceiptManifestItemIds;
    private List<Long> ids;

    private String receiptSlipType;

    private Long long1;
    private Long long2;

    private String siteId;
    private Long facilityId;
    private Long productId;
    private String productStockStatusType;
    private BigDecimal minusQty;
    private BigDecimal decimal1;
    private BigDecimal decimal2;
    private BigDecimal decimal3;

    private int int1;
    private int int2;

    private String string1;
    private String string2;

    private SalesOrderVO salesOrderVO;
    private List<SalesOrderItemVO> salesOrderItemList;
    private List<ReceiptSlipVO> receiptSlipInfos;

    private List<Long> deliveryOrderItemIds;
    private Long locationId;
    private String locationCd;
    private Long productInventoryId;
    private String inventoryTransactionType;
    private Set<Long> storingLineIds;
    private String changeType;
}
