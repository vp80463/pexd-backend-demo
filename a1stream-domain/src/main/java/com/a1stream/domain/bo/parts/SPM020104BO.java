package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020104BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String pickingSeqNo;

    private Long orderPartsId;

    private String orderPartsCd;

    private String orderPartsNm;

    private Long allocatePartsId;

    private String allocatePartsCd;

    private String allocatePartsNm;

    private Long locationId;

    private String location;

    private BigDecimal pickingQty;

    private Long pickingItemId;

}
