package com.a1stream.common.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class SpManifestItemBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "DealerCode")
    private String dealerCode;

    @XmlElement(name = "InvoiceNo")
    private String invoiceNo;

    private String pickingListNo;

    private String orderType;

    @XmlElement(name = "YourOrderNo")
    private String yourOrderNo;

    @XmlElement(name = "PartsNo")
    private String partNo;

    @XmlElement(name = "PartsNoOrdered")
    private String partNoOrdered;

    @XmlElement(name = "Price")
    private BigDecimal receiptPrice;

    @XmlElement(name = "CaseNo")
    private String caseNo;

    @XmlElement(name = "PackingQty")
    private BigDecimal packingQty;

    @XmlElement(name = "ShippedDate")
    private String shippedDate;

    @XmlElement(name = "ShipmentNo")
    private String shipmentNo;

    @XmlElement(name = "InvoiceSeqNo")
    private String invoiceSeqNo;
    
}