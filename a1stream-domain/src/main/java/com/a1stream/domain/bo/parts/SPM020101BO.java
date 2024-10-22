/**
 *
 */
package com.a1stream.domain.bo.parts;

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
public class SPM020101BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String status;
    private String orderStatus;
    private Long salesOrderId;
    private Long orderId;
    private String orderNo;
    private Long consumerId;
    private String consumer;
    private String memo;
    private String shop;
    private String orderDate;
    private String dueDate;
}
