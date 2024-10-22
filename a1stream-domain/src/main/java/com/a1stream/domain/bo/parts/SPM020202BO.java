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
public class SPM020202BO extends BaseBO {

	private static final long serialVersionUID = 1L;
	private String partsCd;
    private Long partsId;
    private String partsNm;
    private Long invoiceItemId;
    private String location;
    private Long locationId;

    private BigDecimal returnPrice;
    private BigDecimal returnPriceNotVat;
    private BigDecimal returnQty;
    private BigDecimal returnAmount;
    private BigDecimal cost;
    private BigDecimal qty;
    private BigDecimal taxRate;

    private Long orderItemId;

    private String salesOrderNo;
    private Long salesOrderId;
    private String orderDate;
    private String orderType;
    private String orderSourceType;
    private String customerOrderNo;

}
