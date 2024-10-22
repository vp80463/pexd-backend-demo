package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM050401BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050401Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private boolean pageFlg = true;

    private Long promotionListId;

    private String periodDateFrom;

    private String periodDateTo;

    private String salesShipmentDateFrom;

    private String salesShipmentDateTo;

    private List<String> salesMethod;

    private Long modelId;

    private String invoiceNo;

    private String duNo;

    private String frameNo;

    private Long pointId;

    private String status;

    private String customer;

    private String pointDesc;

    private String promotion;

    private Long provinceId;

    private Long dealerId;

    private String dealerCd;

    private String userId;

    private String userType;

    private List<SDM050401BO> allTableDataList;

    private String zipNm;

    private String promotionCd;

    private String rejectReason;

    private Long cmmPromotionOrderZipHistoryId;
}
