package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceOrderJobVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceOrderJobId;

    private Long serviceOrderId;

    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    private String serviceCategoryId;

    private String settleTypeId;

    private Long serviceDemandId;

    private String serviceDemandContent;

    private Long serviceOrderFaultId;

    private Long symptomId;

    private Long jobId;

    private String jobCd;

    private String jobNm;

    private BigDecimal stdManhour = BigDecimal.ZERO;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal specialPrice = BigDecimal.ZERO;

    private BigDecimal discountAmt = BigDecimal.ZERO;

    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal vatRate = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    private Long servicePackageId;

    private String servicePackageCd;

    private String servicePackageNm;

    public static ServiceOrderJobVO create(String siteId, Long serviceOrderId) {
        ServiceOrderJobVO entity = new ServiceOrderJobVO();

        entity.setSiteId(siteId);
        entity.setServiceOrderId(serviceOrderId);

        return entity;
    }
}
