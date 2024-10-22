/**
 *
 */
package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
@ExcelIgnoreUnannotated
public class SPQ020403BO extends BaseBO {
	private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0)
    private String sign;

    @ExcelProperty(index = 1)
    private String orderDate;

    @ExcelProperty(index = 2)
    private String orderNo;

    @ExcelProperty(index = 3)
    private String invoiceDate;

    @ExcelProperty(index = 4)
    private String invoiceNo;

    private String partsNo;

    private String partsNm;

    @ExcelProperty(index = 5)
    private String parts;

    @ExcelProperty(index = 6)
    private BigDecimal qty;

    @ExcelProperty(index = 7)
    private BigDecimal retailPrice;

    @ExcelProperty(index = 8)
    private BigDecimal amount;

    private BigDecimal taxRate;

}
