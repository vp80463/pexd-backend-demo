package com.a1stream.domain.bo.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class EInvoiceInvoiceBO {

@XmlElement(name = "CusCode")
    private String cusCode;

    @XmlElement(name = "CusName")
    private String cusName;

    @XmlElement(name = "CusAddress")
    private String cusAddress;

    @XmlElement(name = "CusPhone")
    private String cusPhone;

    @XmlElement(name = "CusTaxCode")
    private String cusTaxCode;

    @XmlElement(name = "PaymentMethod")
    private String paymentMethod;

    @XmlElement(name = "Products")
    private EInvProductsBO products;

    @XmlElement(name = "SST")
    private String sstSign;

    @XmlElement(name = "Total")
    private Integer total;

    @XmlElement(name = "VATRate")
    private Integer vatRate;

    @XmlElement(name = "VATAmount")
    private Integer vatAmount;

    @XmlElement(name = "Amount")
    private Integer amount;

    @XmlElement(name = "AmountInWords")
    private String amountInWords;

    @XmlElement(name = "ArisingDate")
    private String arisingDate;

    @XmlElement(name = "Extra")
    private String extra;

    @XmlElement(name = "LenhDieuDong")
    private String lenhDieuDong;

    @XmlElement(name = "NgayDieuDong")
    private String ngayDieuDong;

    @XmlElement(name = "DonViDieuDong")
    private String donViDieuDong;

    @XmlElement(name = "VeViec")
    private String veViec;

    @XmlElement(name = "NguoiVanChuyen")
    private String nguoiVanChuyen;

    @XmlElement(name = "SoHopDong")
    private String soHopDong;

    @XmlElement(name = "PhuongTienvc")
    private String phuongTienvc;

    @XmlElement(name = "XuatTaiKho")
    private String xuatTaiKho;

    @XmlElement(name = "NhapTaiKho")
    private String nhapTaiKho;

    @XmlElement(name = "PYCGH")
    private String pycgh;

    @XmlElement(name = "PXNDDH")
    private String pxnddh;

    @XmlElement(name = "ProductLine")
    private String productLine;

    @XmlElement(name = "NguonDuLieu")
    private String nguonDuLieu;

    @XmlElement(name = "NoteYamaha")
    private String noteYamaha;

    @XmlElement(name = "USER_ID")
    private String userID;

    @XmlElement(name = "UserName")
    private String userName;

    @XmlElement(name = "Extra3")
    private String extra3;

    @XmlElement(name = "Extra4")
    private String extra4;

    @XmlElement(name = "Extra5")
    private String extra5;

    @XmlElement(name = "Extra6")
    private String extra6;

    @XmlElement(name = "DIACHIGIAO")
    private String diaChiGiao;

    @XmlElement(name = "BillWay")
    private String billWay;

    @XmlElement(name = "SOPHIEUTHU")
    private String sophieuthu;

    @XmlElement(name = "SALEORDER")
    private String saleOrder;

    @XmlElement(name = "DEALERCODE")
    private String dealerCode;

    @XmlElement(name = "CONSIGNEECODE")
    private String consigneeCode;

    @XmlElement(name = "InvoiceCKs")
    private String invoiceCKs;

}
