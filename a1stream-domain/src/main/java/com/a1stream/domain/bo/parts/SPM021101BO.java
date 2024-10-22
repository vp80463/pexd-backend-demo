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
* @author mid2215
*/
@Getter
@Setter
public class SPM021101BO extends BaseBO {

    private Long deliveryOrderId;
    private String inventoryTransactionType;
    private String inventoryTransactionTypeNm;
    private Long customerId;
    private String customerCd;
    private String customerNm;
    private String deliveryOrderNo;
    private Integer lines;
    private BigDecimal totalAmt;
}
