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
public class SdManifestItemBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "SupplierCode")
    private String supplierCd;

    @XmlElement(name = "DealerCode")
    private String dealerCd;

    @XmlElement(name = "ConsigneeCode")
    private String consigneeCd;

    @XmlElement(name = "InternalDeliveryNoteNo")
    private String internalDeliveryNoteNo;

    @XmlElement(name = "ShippingDate")
    private String shippingDate;

    @XmlElement(name = "InvoiceNo")
    private String invoiceNo;

    @XmlElement(name = "ModelCode")
    private String modelCd;

    @XmlElement(name = "ModelName")
    private String modelNm;

    @XmlElement(name = "ColorCode")
    private String colorCd;

    @XmlElement(name = "ColorName")
    private String colorNm;

    @XmlElement(name = "BarCode")
    private String barCd;

    @XmlElement(name = "BatteryCode1")
    private String batteryCd1;

    @XmlElement(name = "BatteryId1")
    private String batteryId1;

    @XmlElement(name = "BatteryCode2")
    private String batteryCd2;

    @XmlElement(name = "BatteryId2")
    private String batteryId2;

    @XmlElement(name = "FrameNo")
    private String frameNo;

    @XmlElement(name = "EngineNo")
    private String engineNo;

    @XmlElement(name = "AssemblyDate")
    private String assemblyDate;

    @XmlElement(name = "SalesPrice")
    private BigDecimal salesPrice;

    @XmlElement(name = "SalesVat")
    private BigDecimal salesVat;

    @XmlElement(name = "EvFlag")
    private String evFlag;
}
