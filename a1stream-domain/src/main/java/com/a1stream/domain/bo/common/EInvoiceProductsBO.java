package com.a1stream.domain.bo.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class EInvoiceProductsBO{

    @XmlElement(name = "Code")
    private String code;

    @XmlElement(name = "ProdName")
    private String prodName;

    @XmlElement(name = "ProdUnit")
    private String prodUnit;

    @XmlElement(name = "ProdQuantity")
    private int prodQuantity;

    @XmlElement(name = "ProdPrice")
    private Integer prodPrice;

    @XmlElement(name = "Amount")
    private Integer amount;

    @XmlElement(name = "IsSum")
    private Integer isSum;

    @XmlElement(name = "Discount")
    private String discount;

    @XmlElement(name = "DiscountAmount")
    private Integer discountAmount;

}
