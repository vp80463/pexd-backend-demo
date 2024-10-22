package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM010202ModelBO;
import com.a1stream.domain.bo.unit.SDM010202ScanBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010202Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long fromPointId;

    private String fromPointCd;

    private String fromPointNm;

    private String fromPointDesc;

    private String shippingTypeId;

    private Long dealerId;

    private String dealerCd;

    private String dealerNm;

    private Long toPointId;

    private String toPointNm;

    private String toPointDesc;

    private String frameNo;

    private String sysDate;
    private String sysTime;

    private Long deliveryOrderId;

    private List<SDM010202ScanBO> scanList;

    private List<SDM010202ModelBO> modelList;

    private List<SDM010202ScanBO> importList;

    private Long otherProperty;

}
