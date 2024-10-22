/**
 *
 */
package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM021303BO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
public class SPM021302Form extends BaseForm {
	private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNo;
    private String customerPromiseDate;
    private String contactContent;
    private String comment;
    private String contactDate;
    private String orderStatus;
    private String orderSourceType;
    private String orderType;
    private String orderDate;
    private String consumer;
    private Long consumerId;
    private String mobilePhone;
    private List<SPM021303BO> content;
}
