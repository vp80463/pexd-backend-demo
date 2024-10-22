package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SalesOrderItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long salesOrderItemId;

    private Long salesOrderId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal specialPrice = BigDecimal.ZERO;;

    private BigDecimal discountOffRate = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    private BigDecimal actualAmt = BigDecimal.ZERO;

    private BigDecimal discountAmt = BigDecimal.ZERO;

    private String productClassification ;

    private BigDecimal orderQty = BigDecimal.ZERO;

    private BigDecimal actualQty = BigDecimal.ZERO;

    private BigDecimal waitingAllocateQty = BigDecimal.ZERO;

    private BigDecimal allocatedQty = BigDecimal.ZERO;

    private BigDecimal instructionQty = BigDecimal.ZERO;

    private BigDecimal shipmentQty = BigDecimal.ZERO;

    private BigDecimal boQty = BigDecimal.ZERO;

    private BigDecimal cancelQty = BigDecimal.ZERO;

    private String cancelDate ;

    private String cancelTime;

    private String cancelReasonType;

    private Long cancelPicId;

    private String cancelPicNm;

    private Long allocatedProductId;

    private String allocatedProductCd;

    private String allocatedProductNm;

    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    private Integer orderPrioritySeq = CommonConstants.INTEGER_ZERO;

    private String boCancelFlag;

    private String boEverFlag;

    private String batteryFlag;

    private Long batteryId;

    private String svClaimFlag;

    private BigDecimal svSellingPrice = BigDecimal.ZERO;;

    private String serviceCategoryId;

    private String settleTypeId;

    private Long serviceDemandId;

    private String serviceDemandContent;

    private Long serviceOrderFaultId;

    private Long symptomId;

    private Long servicePackageId;

    private String servicePackageCd;

    private String servicePackageNm;

    private BigDecimal actualAmtNotVat = BigDecimal.ZERO;

    private String batteryType;

    public static SalesOrderItemVO create(String siteId, Long salesOrderId) {
        SalesOrderItemVO entity = new SalesOrderItemVO();
        entity.setSalesOrderItemId(IdUtils.getSnowflakeIdWorker().nextId());
        entity.setSiteId(siteId);
        entity.setSalesOrderId(salesOrderId);

        return entity;
    }
}
