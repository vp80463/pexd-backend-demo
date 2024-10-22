package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * @author mid2259
 */
public class SDQ010701BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String manifestStatus;
    private Long receiptManifestId;
    private String supplierCd;
    private String supplierNm;
    private String supplierDeliveryDate;
    private String deliveryNoteNo;
    private BigDecimal qty;

}
