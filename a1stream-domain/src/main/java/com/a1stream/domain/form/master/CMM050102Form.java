package com.a1stream.domain.form.master;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.master.CMM050102BO;
import com.a1stream.domain.bo.master.CMM050102PurchaseControlBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050102Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String dataType;
    private Long facilityId;
    private String partsCd;
    private String partsNm;
    private Long partsId;
    private String registrationDate;
    private String localNm;
    private String salesNm;
    private Long middleGroup;
    private String middleGroupCd;
    private Long middleGroupId;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal volumn;

    private BigDecimal stdRetailPrice = BigDecimal.ZERO;
    private String update1;
    private BigDecimal averageCost;
    private String update2;
    private BigDecimal lastCost;
    private String update3;
    private BigDecimal salesLot;
    private BigDecimal minSalesQty;
    private String unavailable;

    private BigDecimal rop;
    private BigDecimal roq;
    private String mainLocation;
    private Long locationId;
    private String brand;
    private BigDecimal purchaseLot;
    private BigDecimal purchaseQty;
    private BigDecimal stdPurchasePrice;
    private String sign;
    private String confirmFinishFlag;

    private List<CMM050102BO> confirmDataList;
    private List<CMM050102PurchaseControlBO> gridDataList;
    private Long pointId;

    private String siteId;

    private List<CMM050102PurchaseControlBO> purchaseControlList;
}
