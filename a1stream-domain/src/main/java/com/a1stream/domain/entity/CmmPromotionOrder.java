package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="cmm_promotion_order")
@Setter
@Getter
public class CmmPromotionOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="promotion_order_id", unique=true, nullable=false)
    private Long promotionOrderId;

    @Column(name="promotion_list_id", nullable=false)
    private Long promotionListId;

    @Column(name="site_nm", nullable=false, length=256)
    private String siteNm;

    @Column(name="facility_id", nullable=false)
    private Long facilityId;

    @Column(name="facility_cd", nullable=false, length=20)
    private String facilityCd;

    @Column(name="facility_nm", nullable=false, length=256)
    private String facilityNm;

    @Column(name="local_order_id", nullable=false)
    private Long localOrderId;

    @Column(name="local_order_no", nullable=false, length=20)
    private String localOrderNo;

    @Column(name="cmm_product_id", nullable=false)
    private Long cmmProductId;

    @Column(name="product_cd", nullable=false, length=40)
    private String productCd;

    @Column(name="product_nm", nullable=false, length=200)
    private String productNm;

    @Column(name="local_serial_product_id", nullable=false)
    private Long localSerialProductId;

    @Column(name="cmm_serial_product_id", nullable=false)
    private Long cmmSerialProductId;

    @Column(name="cmm_customer_id", nullable=false)
    private Long cmmCustomerId;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="local_invoice_id")
    private Long localInvoiceId;

    @Column(name="local_invoice_no", length=40)
    private String localInvoiceNo;

    @Column(name="local_delivery_order_id")
    private Long localDeliveryOrderId;

    @Column(name="local_delivery_order_no", length=40)
    private String localDeliveryOrderNo;

    @Column(name="winner", length=1)
    private String winner;

    @Column(name="sales_method", length=40)
    private String salesMethod;

    @Column(name="sales_pic")
    private Long salesPic;

    @Column(name="invoice_upload_path", length=256)
    private String invoiceUploadPath;

    @Column(name="registration_card_path", length=256)
    private String registrationCardPath;

    @Column(name="gift_receipt_upload_path1", length=256)
    private String giftReceiptUploadPath1;

    @Column(name="gift_receipt_upload_path2", length=256)
    private String giftReceiptUploadPath2;

    @Column(name="gift_receipt_upload_path3", length=256)
    private String giftReceiptUploadPath3;

    @Column(name="lucky_draw_voucher_path", length=256)
    private String luckyDrawVoucherPath;

    @Column(name="id_card_path", length=256)
    private String idCardPath;

    @Column(name="others_path1", length=256)
    private String othersPath1;

    @Column(name="others_path2", length=256)
    private String othersPath2;

    @Column(name="others_path3", length=256)
    private String othersPath3;

    @Column(name="jugement_status", length=40)
    private String jugementStatus;

    @Column(name="reject_reason", length=256)
    private String rejectReason;

    @Column(name="can_enjoy_promotion", length=1)
    private String canEnjoyPromotion;

    @Column(name="invoice_memo", length=256)
    private String invoiceMemo;

    @Column(name="link_memo", length=256)
    private String linkMemo;

    @Column(name="verification_code_memo", length=256)
    private String verificationCodeMemo;


}
