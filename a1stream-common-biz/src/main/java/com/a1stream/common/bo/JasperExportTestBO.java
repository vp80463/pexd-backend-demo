package com.a1stream.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class JasperExportTestBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 出库单号
     */
    private String deliveryNo;

    /**
     * 经销商
     */
    private String dealerDescription;

    /**
     * 收货人
     */
    private String toAddress;

    /**
     * 出库日期
     */
    private String date;

    /**
     * logo
     */
    private String logo;

    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 条形码
     */
    private String barCode;

    private List<JasperExportDetailTestBO> detailTestList;
}