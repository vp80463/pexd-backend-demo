/**
 *
 */
package com.a1stream.domain.bo.parts;

import java.util.List;

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
public class SPM020201BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String invoiceNo;
    private Long invoiceId;
    private String reason;
    private String point;
    private Long pointId;
    private String customer;
    private String customerCd;
    private String customerNm;
    private Long customerId;
    private String returnInvoiceNo;
    private Long returnInvoiceId;
    private String returnByInvoice;
    private String invoiceDate;
    private String returnByInvoiceInvisible;
    private Long fromOrganizationId;
    private Long toOrganizationId;
    private String orderToType;
    private List<SPM020202BO> content;
}
