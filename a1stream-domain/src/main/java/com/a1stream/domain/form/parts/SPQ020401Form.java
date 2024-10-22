/**
 *
 */
package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.parts.SPQ020402BO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
public class SPQ020401Form extends BaseForm {
	private static final long serialVersionUID = 1L;

    private String point;
    private Long pointId;
    private String dateFrom;
    private String dateTo;
    private String invoiceNo;
    private String summaryFlag;

    private BaseTableData<SPQ020402BO> content;

}
