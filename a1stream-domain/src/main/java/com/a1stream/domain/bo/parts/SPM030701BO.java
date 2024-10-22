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
public class SPM030701BO extends BaseBO {
	private static final long serialVersionUID = 1L;

    private BigDecimal currentStockQty;

    private Long fromLocationId;
    private Long fromBinTypeId;
    private String fromLocation;
    private BigDecimal averageCost;


}
