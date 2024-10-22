/**
 *
 */
package com.a1stream.domain.form.parts;

import java.math.BigDecimal;

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
public class SPM030701Form extends BaseForm {
	private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointNm;
    private String pointCd;

    private Long partsId;
    private String partsNm;
    private String partsCd;
    private Long mainLocationId;

    private String reason;
    private String adjustmentType;
    private String checkForMinus;

    private BigDecimal currentStockQty;
    private BigDecimal adjustmentQty;
    private BigDecimal averageCost;
    private BigDecimal partsCost;

    private String fromLocation;
    private Long fromLocationId;

    private String toLocation;
    private Long toLocationId;

}
