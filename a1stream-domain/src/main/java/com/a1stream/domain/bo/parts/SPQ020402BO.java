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
public class SPQ020402BO extends BaseBO {
	private static final long serialVersionUID = 1L;

    private Long invoiceId;

    @ExcelProperty(index = 0)
    private String orderDate;

    @ExcelProperty(index = 1)
    private String orderNo;

    @ExcelProperty(index = 2)
    private String invoiceDate;

    @ExcelProperty(index = 3)
    private String invoiceNo;

    @ExcelProperty(index = 4)
    private String vatNo;

    @ExcelProperty(index = 5)
    private String serialNo;

    @ExcelProperty(index = 6)
    private String customer;

    @ExcelProperty(index = 7)
    private String phoneNo;

    @ExcelProperty(index = 8)
    private String address;

    @ExcelProperty(index = 9)
    private String taxCode;

    @ExcelProperty(index = 10)
    private BigDecimal shipmentLines;

    @ExcelProperty(index = 11)
    private BigDecimal salesOrder;

    @ExcelProperty(index = 12)
    private BigDecimal serviceOrder;

    @ExcelProperty(index = 13)
    private BigDecimal serviceCharge;

    @ExcelProperty(index = 14)
    private BigDecimal amount;

    @ExcelProperty(index = 15)
    private String cashier;

}
