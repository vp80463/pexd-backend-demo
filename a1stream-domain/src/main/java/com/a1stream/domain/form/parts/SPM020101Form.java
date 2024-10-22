/**
 *
 */
package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
public class SPM020101Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private String deliveryPoint;
    private Long deliveryPointId;

    private String point;
    private Long pointId;

    private String dateFrom;
    private String dateTo;

    private String orderNo;

    private List<String> status;

    private String unfinishedOnly;

    private String customer;
    private Long customerId;

    private String dateStart;
    private String dateEnd;

    private boolean pageFlg = true;

}
