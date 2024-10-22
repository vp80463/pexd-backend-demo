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
public class SPM021302BO extends BaseBO {
	private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderStatus;
    private String orderSourceType;
    private String orderType;
    private String customerPromiseDate;
    private String orderNo;
    private String orderDate;
    private String consumer;
    private Long consumerId;
    private String mobilePhone;
    private String contactContent;
    private String comment;
    private String contactDate;
    private List<SPM021303BO> content;
}
