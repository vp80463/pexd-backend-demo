/**
 *
 */
package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
public class SPM021303BO extends BaseBO {
	private static final long serialVersionUID = 1L;

    private Long partsId;
    private String partsNo;
    private String partsNm;
    private Long allocatedPartsId;
    private String allocatedPartsCd;
    private String allocatedPartsNm;
    private BigDecimal orderQty;
    private BigDecimal boQty;
    private BigDecimal allocatedQty;
    private BigDecimal onPickingQty;
    private BigDecimal shippedQty;

    //service
    private BigDecimal yamahaBO;
    private BigDecimal yamahaAllocated;
    private BigDecimal yamahaOnShipping;
    private BigDecimal yamahaInvoiced;
    private BigDecimal yamahaCancelled;
    private String atpStatus;
    private String targetDeliveryDate;
    private String targetArrivalDate;



}
