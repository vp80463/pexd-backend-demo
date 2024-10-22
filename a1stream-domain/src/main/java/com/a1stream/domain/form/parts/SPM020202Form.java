/**
 *
 */
package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2330
*/
@Getter
@Setter
public class SPM020202Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private String invoiceNo;

    private Long invoiceId;

    private String returnInvoiceNo;

    private Long returnInvoiceId;

    private String point;

    private Long pointId;

    private String customer;

    private Long customerId;

    private String dateFrom;

    private String dateTo;
}
