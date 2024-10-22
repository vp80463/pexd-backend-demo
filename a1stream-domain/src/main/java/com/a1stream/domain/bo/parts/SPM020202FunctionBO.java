/**
 *
 */
package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:spm020202 Sales Return History Inquiry
*
* @author mid2330
*/
@Getter
@Setter
public class SPM020202FunctionBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointCd;
    private String pointNm;
    private String invoiceNo;
    private String returnInvoiceNo;
    private Long customerId;
    private String customerCd;
    private String customerNm;
    private BigDecimal returnAmt = BigDecimal.ZERO;
    private String returnDate;
    private Long returnInvoiceId;

    private Long partsId;
    private String partsCd;
    private String partsNm;
    private BigDecimal returnQty = BigDecimal.ZERO;
    private BigDecimal returnPrice = BigDecimal.ZERO;
    private BigDecimal returnAmount = BigDecimal.ZERO;
    private BigDecimal cost = BigDecimal.ZERO;

}
