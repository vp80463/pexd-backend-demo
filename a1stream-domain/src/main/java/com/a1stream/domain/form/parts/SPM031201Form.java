package com.a1stream.domain.form.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SPM031201Form extends BaseForm {
	private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointNm;
    private String pointCd;

    private Long partsId;
    private String partsNm;
    private String partsCd;

    private String mainLocation;

    private String releaseType;

    private String fromLocation;
    private Long fromLocationId;
    private BigDecimal fromLocationQty;

    private BigDecimal releaseQty;

    private String toLocation;
    private Long toLocationId;

}
