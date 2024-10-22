package com.a1stream.common.bo;

import com.a1stream.common.model.BaseBO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class UnitRetailCreateOrderBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1080647058366570449L;

    /**
     * 订单id
     */
    private Long salesOrderId;

    /**
     * 最终客户ID
     */
    private Long ownerConsumerId;

    /**
     * 最终用户顾客ID
     */
    private Long userConsumerId;

    /**
     * 用户名称
     */
    private String personName;

    /**
     * 用户id
     */
    private Long personId;

    /**
     * 当前据点id
     */
    private Long currentPointUid;

    /**
     * 站点id
     */
    private Long pointId;

    /**
     * 邮件地址
     */
    private String email;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 税号
     */
    private String cusTaxCode;

    /**
     * 地址
     */
    private String address;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 礼品说明
     */
    private String giftDescription;

    /**
     * 站点地址
     */
    private String pointAddress;

    /**
     * 电池id1
     */
    private Long batteryId1;

    /**
     * 电池id2
     */
    private Long batteryId2;

    /**
     * 型号id
     */
    private Long mode1Id;

    /**
     * 型号cd
     */
    private String modelCd;

    /**
     * 型号名称
     */
    private String modelName;

    /**
     * 车架号
     */
    private String frameNumber;

    /**
     * 发动机号
     */
    private String engineNumber;

    /**
     * 颜色名称
     */
    private String colorName;

    /**
     * 产品id
     */
    private Long serializedProductId;

    /**
     * 标准价格
     */
    private BigDecimal standardPrice;

    /**
     * 销售价格
     */
    private BigDecimal sellingPrice;

    /**
     * 特殊价格
     */
    private BigDecimal specialPrice;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmt;

    /**
     * 折扣率
     */
    private BigDecimal discountOffRate;

    /**
     * 税率
     */
    private BigDecimal taxRate;
}