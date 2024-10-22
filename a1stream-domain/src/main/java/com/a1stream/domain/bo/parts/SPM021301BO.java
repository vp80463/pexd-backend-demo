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
public class SPM021301BO extends BaseBO {

    private static final long serialVersionUID = 1L;
    private String orderNo;
    private Long orderId;
    private Long serviceId;
    private String orderDate;
    private String customerPromiseDate;
    private String contactDate;
    private String consumer;
    private Long consumerId;
    private String mobile;
    private String orderSourceType;
    private BigDecimal orderLines;
    private BigDecimal boLines;
    private String boRelease;

}
