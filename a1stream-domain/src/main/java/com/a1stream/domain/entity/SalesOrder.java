package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="sales_order")
@Setter
@Getter
public class SalesOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="sales_order_id", unique=true, nullable=false)
    private Long salesOrderId;

    @Column(name="order_no", length=40)
    private String orderNo;

    @Column(name="proforma_no", length=40)
    private String proformaNo;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="proforma_order_date", length=8)
    private String proformaOrderDate;

    @Column(name="delivery_plan_date", length=8)
    private String deliveryPlanDate;

    @Column(name="ship_date", length=8)
    private String shipDate;

    @Column(name="allocate_due_date", length=8)
    private String allocateDueDate;

    @Column(name="order_status", length=40)
    private String orderStatus;

    @Column(name="order_to_type", length=40)
    private String orderToType;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="order_priority_type", length=40)
    private String orderPriorityType;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;

    @Column(name="entry_facility_id")
    private Long entryFacilityId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="facility_address_id", length=40)
    private String facilityAddressId;

    @Column(name="facility_address", length=256)
    private String facilityAddress;

    @Column(name="customer_if_flag", length=1)
    private String customerIfFlag;

    @Column(name="related_order_no", length=40)
    private String relatedOrderNo;

    @Column(name="drop_ship_type", length=40)
    private String dropShipType;

    @Column(name="order_type", length=40)
    private String orderType;

    @Column(name="bo_cancel_flag", length=1)
    private String boCancelFlag;

    @Column(name="ship_complete_flag", length=1)
    private String shipCompleteFlag;

    @Column(name="demand_exception_flag", length=1)
    private String demandExceptionFlag;

    @Column(name="acc_eo_flag", length=1)
    private String accEoFlag;

    @Column(name="bo_flag", length=1)
    private String boFlag;

    @Column(name="bo_contact_content_type", length=40)
    private String boContactContentType;

    @Column(name="bo_contact_date", length=8)
    private String boContactDate;

    @Column(name="bo_contact_time", length=8)
    private String boContactTime;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="consignee_id")
    private Long consigneeId;

    @Column(name="consignee_person", length=180)
    private String consigneePerson;

    @Column(name="consignee_mobile_phone", length=20)
    private String consigneeMobilePhone;

    @Column(name="consignee_addr", length=256)
    private String consigneeAddr;

    @Column(name="cmm_consumer_id")
    private Long cmmConsumerId;

    @Column(name="consumer_vip_no", length=20)
    private String consumerVipNo;

    @Column(name="consumer_nm_first", length=60)
    private String consumerNmFirst;

    @Column(name="consumer_nm_middle", length=60)
    private String consumerNmMiddle;

    @Column(name="consumer_nm_last", length=60)
    private String consumerNmLast;

    @Column(name="consumer_nm_full", length=180)
    private String consumerNmFull;

    @Column(name="email", length=100)
    private String email;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="customer_tax_code", length=100)
    private String customerTaxCode;

    @Column(name="address", length=256)
    private String address;

    @Column(name="entry_pic_id")
    private Long entryPicId;

    @Column(name="entry_pic_nm", length=60)
    private String entryPicNm;

    @Column(name="sales_pic_id")
    private Long salesPicId;

    @Column(name="sales_pic_nm", length=60)
    private String salesPicNm;

    @Column(name="authorize_number", length=40)
    private String authorizeNumber;

    @Column(name="payment_method_type", length=40)
    private String paymentMethodType;

    @Column(name="discount_off_rate", precision=5)
    private BigDecimal discountOffRate = BigDecimal.ZERO;

    @Column(name="tax_rate", precision=5)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="invoice_print_flag", length=1)
    private String invoicePrintFlag;

    @Column(name="deposit_amt", precision=18)
    private BigDecimal depositAmt = BigDecimal.ZERO;

    @Column(name="total_qty", precision=18)
    private BigDecimal totalQty = BigDecimal.ZERO;

    @Column(name="total_amt", precision=18)
    private BigDecimal totalAmt = BigDecimal.ZERO;

    @Column(name="total_actual_qty", precision=18)
    private BigDecimal totalActualQty = BigDecimal.ZERO;

    @Column(name="total_actual_amt", precision=18)
    private BigDecimal totalActualAmt = BigDecimal.ZERO;

    @Column(name="gift_description", length=256)
    private String giftDescription;

    @Column(name="facility_multi_addr", length=25)
    private String facilityMultiAddr;

    @Column(name="special_reduce_flag", length=1)
    private String specialReduceFlag;

    @Column(name="ev_order_flag", length=1)
    private String evOrderFlag;

    @Column(name="model_cd", length=40)
    private String modelCd;

    @Column(name="model_nm", length=256)
    private String modelNm;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="engine_no", length=40)
    private String engineNo;

    @Column(name="warranty_no", length=40)
    private String warrantyNo;

    @Column(name="color_nm", length=40)
    private String colorNm;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="model_type", length=40)
    private String modelType;

    @Column(name="displacement")
    private Integer displacement = CommonConstants.INTEGER_ZERO;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="related_sv_order_id")
    private Long relatedSvOrderId;

    @Column(name="order_for_employee_flag", length=1)
    private String orderForEmployeeFlag;

    @Column(name="employee_cd", length=40)
    private String employeeCd;

    @Column(name="employee_relation_ship", length=40)
    private String employeeRelationShip;

    @Column(name="ticket_no", length=40)
    private String ticketNo;

    @Column(name="customer_cd", length=40)
    private String customerCd;

    @Column(name="total_actual_amt_not_vat", precision=18, scale=2)
    private BigDecimal totalActualAmtNotVat = BigDecimal.ZERO;

    @Column(name="total_line")
    private Integer totalLine = CommonConstants.INTEGER_ZERO;

    @Column(name="user_consumer_id")
    private Long userConsumerId;

    @Column(name="employee_nm", length=20)
    private String employeeNm;
}
