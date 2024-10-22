package com.a1stream.common.constants;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.model.ConstantsBO;

public class PJConstants {

    // AWS Queue 相关静态变量
    public static final class AwsSqs {

        public static final String QUEUE_URLS     = "QueueUrls";
        public static final String RECEIPT_HANDLE = "ReceiptHandle";
        public static final String MESSAGE_BODY   = "Body";
    }

    public static final class BrandType {
        //不允许随意修改，codeDbid必须和mst_brand表中的key保持一致
        public static final ConstantsBO YAMAHA     = new ConstantsBO("1", "YAMAHA", "YAMAHA", 1);
        public static final ConstantsBO YAMAHAS    = new ConstantsBO("2", "YAMAHA(S)", "YAMAHA(S)", 2);
        public static final ConstantsBO HONDA      = new ConstantsBO("3", "HONDA", "HONDA", 3);
        public static final ConstantsBO KIMOCO     = new ConstantsBO("4", "KIMOCO", "KIMOCO", 4);
        public static final ConstantsBO NOBIKE     = new ConstantsBO("5", "NOBIKE", "NOBIKE", 5);
        public static final ConstantsBO OTHER      = new ConstantsBO("6", "OTHER", "Other", 6);
        public static final ConstantsBO PIAGGIO    = new ConstantsBO("7", "PIAGGIO", "PIAGGIO", 7);
        public static final ConstantsBO SUZUKI     = new ConstantsBO("8", "SUZUKI", "SUZUKI", 8);
        public static final ConstantsBO SYM        = new ConstantsBO("9", "SYM", "SYM", 9);
    }

    public static final class CategoryCd {

        public static final String AT             = "AT";
        public static final String MP             = "MP";
        public static final String BIGBIKE        = "BIGBIKE";
        public static final String EV             = "EV";
    }

    public static final class ServiceDemand {
        public static final String COUPON_LEVEL_ONE = "Phiếu số 1";
    }

    public static final class DemandSource {

        public static final String ORIGINALDEMAND     = "Original Demand";
        public static final String CURRENTDEMAND      = "Current Demand";
    }

    public static final class ReleaseType {
        public static final String LOCATIONSTORAGE     = "Location Storage";
        public static final String SCRAPPING           = "Scrapping";
    }

    public static final class StockTakingType {

        public static final String SYSTEM_TOTAL          = "System Total";
        public static final String ACTUAL_TOTAL          = "Actual Total";
        public static final String QTYEQUAL              = "Actual Qty = System Qty";
        public static final String QTYEXCEED             = "Actual Qty > System Qty";
        public static final String QTYLACK               = "Actual Qty < System Qty";
        public static final String ACCURACY_PERCCENT     = "Accuracy% = (Actual Qty = System Qty)/Actual Total";
    }

    public static final class FacilityType {

        public static final String ALL          = "ALL";
        public static final String WO           = "WO";
        public static final String SHOP         = "SHOP";
        public static final String SHOP_ALL     = "SHOP_ALL";
        public static final String CONSIGNEE    = "CONSIGNEE";
    }

    public static final class InOutType {

        public static final String IN             = "IN";
        public static final String OUT            = "OUT";
    }

    public static final class JudgementStatusType {

        public static final String DEALER         = "DEALER";
        public static final String YMVNSDACCT     = "YMVNSDACCT";
        public static final String YMVNSD         = "YMVNSD";
        public static final String ACCT           = "ACCT";
    }

    public static final class UploadAccCatalogType {

        public static final String PDF_URL         = "PDFURL";
        public static final String IMG_URL         = "IMGURL";
        public static final String IMAGE_BASE      = "IMGURLBASE64";
    }

    public static final class EInoviceMasterType {

        public static final String CHAR_ACCOUNT        = "account";
        public static final String CHAR_ACPASS         = "acpass";
        public static final String CHAR_AREA           = "area";
        public static final String CHAR_CONVERT        = "convert";
        public static final String CHAR_PATTERNSD      = "patternsd";
        public static final String CHAR_PATTERNSPSV    = "patternspsv";
        public static final String CHAR_SERIALSD       = "serialsd";
        public static final String CHAR_SERIALSPSV     = "serialspsv";
        public static final String CHAR_USERNAME       = "username";
        public static final String CHAR_USERPASS       = "userpass";

    }

    public static final class RoleCode {

        public static final String ACCOUNT              = "ACCOUNT";
        public static final String ADM                  = "ADM";
        public static final String IT                   = "IT";
        public static final String KINHDOANH            = "KINHDOANH";
        public static final String MANAGER              = "MANAGER";
        public static final String MECHANIC             = "MECHANIC";
        public static final String OWNER                = "OWNER";
        public static final String SALESPLANNING        = "SALES_PLANNING";
        public static final String SERIALCOUNTER        = "SERIAL_COUNTER";
        public static final String SERIALLEADER         = "SERIAL_LEADER";
        public static final String SERVICECOUNTER       = "SERVICE_COUNTER";
        public static final String SERVICERECEP         = "SERVICE_RECEP";
        public static final String SPAREPART            = "SPARE_PART";
        public static final String SPAREPARTL           = "SPARE_PART_L";
        public static final String YMVN                 = "YMVN";
        public static final String YMVNSD               = "YMVN_SD";
        public static final String YMVNSP               = "YMVN_SP";
        public static final String YMVNSTAFF            = "YMVN_STAFF";
        public static final String YMVNSV               = "YMVN_SV";
        public static final String ACCOUNTINGPROMOTION  = "ACCOUNTING_PROMOTION";

    }

    public static final class RoleType {
        public static final String SALESCOMPANY         = "SALESCOMPANY"; //666N
        public static final String DOSHOP               = "DOSHOP";
        public static final String SHOP3S               = "3SSHOP";
        public static final String SYSSE                = "SYSSE";    //YMSLX
        public static final String SPESHOP              = "SPECIALSHOP"; //YT00
    }

    public static final class SalesOrderActionType {

        public static final String PRE_ALLOCATE            = "PRE_ALLOCATE";
        public static final String STOCK_ALLOCATE          = "STOCK_ALLOCATE";
        public static final String ORDER_CANCEL            = "ORDER_CANCEL";
        public static final String PICKING_INSTRUCTION     = "PICKING_INSTRUCTION";
        public static final String SHIPPING_COMPLETION     = "SHIPPING_COMPLETION";
    }

    public static final class SpecialServiceDemandJobCodeType {

        public static final String KEY_FS01 = "FS01";
        public static final String KEY_FS02 = "FS02";
        public static final String KEY_FS03 = "FS03";

    }

    public static final class SpecialClaimSymptonCondition {

        public static final String KEY_DEFAULT_SYMPTON       = "99";
        public static final String KEY_DEFAULT_CONDITION     = "99";
    }

    public static final Map<String, Field[]> ConstantsFieldMap = Map.ofEntries(

            Map.entry("S001", ProductClsType.class.getDeclaredFields()),
            Map.entry("S005", GenderType.class.getDeclaredFields()),
            Map.entry("S012", ServiceCategory.class.getDeclaredFields()),
            Map.entry("S015", SalesOrderStatus.class.getDeclaredFields()),
            Map.entry("S022", ConsumerSerialProRelationType.class.getDeclaredFields()),
            Map.entry("S026", ServiceRequestStatus.class.getDeclaredFields()),
            Map.entry("S027", InventoryTransactionType.class.getDeclaredFields()),
            Map.entry("S028", ServicePaymentStatus.class.getDeclaredFields()),
            Map.entry("S032", ServiceOrderStatus.class.getDeclaredFields()),
            Map.entry("S038", InvoiceType.class.getDeclaredFields()),
            Map.entry("S048", RegistrationDocumentFeatrueCategory.class.getDeclaredFields()),
            Map.entry("S049", ReceiptSlipStatus.class.getDeclaredFields()),
            Map.entry("S043", PurchaseOrderPriorityType.class.getDeclaredFields()),
            Map.entry("S045", PurchaseMethodType.class.getDeclaredFields()),
            Map.entry("S050", ManifestStatus.class.getDeclaredFields()),
            Map.entry("S066", Relationship.class.getDeclaredFields()),
            Map.entry("S062", ConsumerPrivacyAgreementType.class.getDeclaredFields()),
            Map.entry("S057", ReturnRequestStatus.class.getDeclaredFields()),
            Map.entry("S058", ReturnRequestType.class.getDeclaredFields()),
            Map.entry("S059", SalesLeadContactStatus.class.getDeclaredFields()),
            Map.entry("S064", SalesLead.class.getDeclaredFields()),
            Map.entry("S065", RemindType.class.getDeclaredFields()),
            Map.entry("S077", ContactStatus2s.class.getDeclaredFields()),
            Map.entry("S078", LeadStatus2s.class.getDeclaredFields()),
            Map.entry("S082", OrderType.class.getDeclaredFields()),
            Map.entry("S090", ReservationStatus.class.getDeclaredFields()),
            Map.entry("S091", ReservationServiceContents.class.getDeclaredFields()),
            Map.entry("S092", ReservationMethod.class.getDeclaredFields()),
            Map.entry("S097", QueueStatus.class.getDeclaredFields()),
            Map.entry("S099", PurchaseType.class.getDeclaredFields()),
            Map.entry("BATTERY", BatteryType.class.getDeclaredFields())

        );

    public static final class VatCode {

        public static final ConstantsBO VAT0 = new ConstantsBO("0", "0", 1);
        public static final ConstantsBO VAT1 = new ConstantsBO("0.1", "0.1", 2);
    }

    public class SpSalesStatusType {

        public static final ConstantsBO SALESSTATUS_VALUE_NORMAL    = new ConstantsBO("0", "Normal", 1);
        public static final ConstantsBO SALESSTATUS_VALUE_STOCKONLY = new ConstantsBO("1", "Stock Only", 2);
        public static final ConstantsBO SALESSTATUS_VALUE_NOTSALES  = new ConstantsBO("2", "Not For Sale", 3);
    }

    public static final class BatteryType {

        public static final String CODE_ID = "BATTERY";

        public static final ConstantsBO TYPE1 = new ConstantsBO("battery1", "battery1", 1);
        public static final ConstantsBO TYPE2 = new ConstantsBO("battery2", "battery2", 2);
    }

    /**
     * Constants: SXXX
     */

    public static final class ProductClsType {

        public static final String CODE_ID = "S001";

        public static final ConstantsBO GOODS   = new ConstantsBO("S001GOODS", "Xe", "SD", 1);
        public static final ConstantsBO PART    = new ConstantsBO("S001PART", "Phụ tùng", "SP", 2);
        public static final ConstantsBO SERVICE = new ConstantsBO("S001SERVICE", "Dịch vụ", "SV", 3);
    }

    public static final class OrgRelationType {

        public static final ConstantsBO COMPANY    = new ConstantsBO("S002COMPANY", "", "S025COMPANY", 1);
        public static final ConstantsBO DEPARTMENT = new ConstantsBO("S002DEPARTMENT", "", "S025DEPARTMENT", 2);
        public static final ConstantsBO SUPPLIER   = new ConstantsBO("S002SUPPLIER", "Nhà cung cấp", "S025SUPPLIER", 3);
        public static final ConstantsBO DEALER     = new ConstantsBO("S002DEALER", "Khách hàng", "S025CUSTOMER", 4);
        public static final ConstantsBO CONSUMER   = new ConstantsBO("S002CONSUMER", "Consumer", "S025CUSTOMER", 5);
    }

    public static final class FacilityRoleType {

        public static final String SHOP      = "S003SHOP";
        public static final String CONSIGNEE = "S003CONSIGNEE";
    }

    public static final class EffectStatus {

        public static final String CODE_ID = "S004";

        public static final ConstantsBO EFFECTIVE   = new ConstantsBO("S004EFFECTIVE", "Effective", 1);
        public static final ConstantsBO INEFFECTIVE = new ConstantsBO("S004INEFFECTIVE", "Ineffective", 2);
    }

    public static final class GenderType {
        public static final String CODE_ID = "S005";

        public static final ConstantsBO MALE   = new ConstantsBO("S005MALE", "Male", 1);
        public static final ConstantsBO FEMALE = new ConstantsBO("S005FEMALE", "Female", 2);

        public static final Map<String, String> ifTransfer = Map.of("P", "S005FEMALE", "L", "S005MALE");
    }

    public static final class LocationType {

        public static final String CODE_ID = "S006";

        public static final ConstantsBO TENTATIVE = new ConstantsBO("S006TENTATIVE", "TENTATIVE","BO Location", 1);
        public static final ConstantsBO NORMAL    = new ConstantsBO("S006NORMAL", "NORMAL", "Normal Location",2);
        public static final ConstantsBO SERVICE   = new ConstantsBO("S006SERVICE", "SERVICE","Service Work Location", 3);
        public static final ConstantsBO FROZEN    = new ConstantsBO("S006FROZEN", "FROZEN","Frozen Location", 4);
    }

    public static final class PartsCategory {

        public static final String LARGEGROUP  = "S008LARGEGROUP";
        public static final String MIDDLEGROUP = "S008MIDDLEGROUP";
        public static final String SDSCATEGORY = "S009SDSCATEGORY";
    }

    public static final class ProductRelationClass {

        public static final String SUPERSEDING = "S010SUPERSEDING";
    }

    public static final class CostType {

        public static final String AVERAGE_COST = "S011AVERAGECOST";
        public static final String RECEIVE_COST = "S011RECEIVECOST";
    }

    public static final class ServiceCategory {
        //codeData2 = settleType
        //codeData3 = ClaimType to SW
        public static final String CODE_ID = "S012";

        public static final ConstantsBO REPAIR       = new ConstantsBO("S012REPAIR", "Sửa chữa", "S013CUSTOMER", "", 1);
        public static final ConstantsBO PDI          = new ConstantsBO("S012PDI", "PDI", "S013FACTORY", "", 2);
        public static final ConstantsBO CLAIM        = new ConstantsBO("S012CLAIM", "Bảo hành", "S013FACTORY", "REGULAR", 3);
        public static final ConstantsBO FREECOUPON   = new ConstantsBO("S012FREECOUPON", "Bảo trì miễn phí", "S013FACTORY", "", 4);
        public static final ConstantsBO CLAIMBATTERY = new ConstantsBO("S012CLAIMBATTERY", "Claim for Battery", "S013FACTORY", "REGULARFORBATTERY", 5);
        public static final ConstantsBO QUICKSERVICE = new ConstantsBO("S012QUICKSERVICE", "Quick Service", "S013CUSTOMER", "", 6);
        public static final ConstantsBO FREESERVICE  = new ConstantsBO("S012FREESERVICE", "Miễn phí", "S013SHOP", "", 7);
        public static final ConstantsBO SPECIALCLAIM = new ConstantsBO("S012SPECIALCLAIM", "Chương trình DV", "S013FACTORY", "CAMPAIGN", 8);
    }

    public static final class SettleType {

        public static final String CODE_ID = "S013";

        public static final ConstantsBO SHOP      = new ConstantsBO("S013SHOP", "Đại lý", 1);
        public static final ConstantsBO CUSTOMER  = new ConstantsBO("S013CUSTOMER", "Khách hàng", 2);
        public static final ConstantsBO FACTORY   = new ConstantsBO("S013FACTORY", "Yamaha VN", 3);
        public static final ConstantsBO INSURANCE = new ConstantsBO("S013INSURANCE", "Bảo hiểm", 4);
        public static final ConstantsBO TOTAL     = new ConstantsBO("SUM", "Total", 5); // 列合计行
    }

    public static class SalesOrderStatus {

        public static final String CODE_ID                          = "S015";
        /**
         * GOODS
         */
        public static final ConstantsBO PROFORMA = new ConstantsBO("S015PROFORMA", "Proforma", 1);
        public static final ConstantsBO WAITING_ALLOCATE = new ConstantsBO("S015WAITINGALLOCATE", "Waiting Allocate", 2);
        public static final ConstantsBO WAITING_SHIPPING = new ConstantsBO("S015WAITINGSHIPPING", "Waiting Shipping", 3);
        public static final ConstantsBO INSTRUCTION = new ConstantsBO("S015INSTRUCTION", "Instruction", 4);
        public static final ConstantsBO SHIPPED = new ConstantsBO("S015SHIPPED", "Shipped", 5);
        public static final ConstantsBO CANCELLED = new ConstantsBO("S015CANCELLED", "Cancelled", 6);
        public static final ConstantsBO ONSHIPPING = new ConstantsBO("S015ONSHIPPING", "On Shipping", 7);
        /**
         * PART
         */
        public static final ConstantsBO SP_CREATED = new ConstantsBO("S015SPCREATED", "Created", 8);
        public static final ConstantsBO SP_PROFORMA = new ConstantsBO("S015SPPROFORMA", "Proforma", 9);
        public static final ConstantsBO SP_WAITINGFORRELEASE = new ConstantsBO("S015SPWAITINGFORRELEASE", "Waiting For Release", 10);
        public static final ConstantsBO SP_WAITINGFORAPPROVE = new ConstantsBO("S015SPWAITINGFORAPPROVE", "Waiting For Approve", 11);
        public static final ConstantsBO SP_WAITINGALLOCATE = new ConstantsBO("S015SPWAITINGALLOCATE", "Waiting Allocate", 12);
        public static final ConstantsBO SP_WAITINGPICKING = new ConstantsBO("S015SPWAITINGPICKING", "Waiting Picking", 13);
        public static final ConstantsBO SP_ONPICKING = new ConstantsBO("S015SPONPICKING", "On Picking", 14);
        public static final ConstantsBO SP_SHIPMENTED = new ConstantsBO("S015SPSHIPMENTED", "Shipmented", 15);
        public static final ConstantsBO SP_CANCELLED = new ConstantsBO("S015SPCANCELLED", "Cancelled", 16);
    }

    public static final class FinishStatus {

        public static final String CODE_ID  = "S016";

        public static final ConstantsBO UN_FINISHED = new ConstantsBO("S016UNFINISHED", "Unfinished", 1);
    }

    public static final class SalesOrderPriorityType {

        public static final String CODE_ID = "S017";

        public static final ConstantsBO SOEO = new ConstantsBO("S017SOEO", "EO", 1);
        public static final ConstantsBO SORO = new ConstantsBO("S017SORO", "RO", 2);
    }

    public static final class SpStockStatus {

        public static final String CODE_ID = "S018";

        public static final ConstantsBO ONHAND_QTY         = new ConstantsBO("S018ONHANDQTY"       , "On Hand Qty", 1);
        public static final ConstantsBO ONSERVICE_QTY      = new ConstantsBO("S018ONSERVICEQTY"    , "On Service Qty", 2);
        public static final ConstantsBO ONFROZEN_QTY       = new ConstantsBO("S018ONFROZENQTY"     , "On Frozen Qty", 3);
        public static final ConstantsBO ONRECEIVING_QTY    = new ConstantsBO("S018ONRECEIVINGQTY"  , "On Receiving Qty", 21);
        public static final ConstantsBO ONPICKING_QTY      = new ConstantsBO("S018ONPICKINGQTY"    , "On Picking Qty", 22);
        public static final ConstantsBO ONCANVASSING_QTY   = new ConstantsBO("S018ONCANVASSINGQTY" , "On Canvassing Qty", 25);
        public static final ConstantsBO ONTRANSFER_IN_QTY  = new ConstantsBO("S018ONTRANSFERINQTY" , "On Transfer In Qty", 27);
        public static final ConstantsBO ONTRANSFER_OUT_QTY = new ConstantsBO("S018ONTRANSFEROUTQTY", "On Transfer Out Qty", 28);
        public static final ConstantsBO ONSHIPPING         = new ConstantsBO("S018ONSHIPPING"      , "On Shipping", 16);
        public static final ConstantsBO ONDELIVERY_QTY     = new ConstantsBO("S018ONDELIVERYQTY"   , "On Delivery Qty", 9);
        public static final ConstantsBO ONBORROWING_QTY    = new ConstantsBO("S018ONBORROWINGQTY"  , "On Borrowing Qty", 10);

        public static final ConstantsBO ONISSUING_PURCHASE_RO_QTY = new ConstantsBO("S018ONISSUINGPURCHASEROQTY", "EO On Issueing Purchase Qty", 6);
        public static final ConstantsBO ONISSUING_PURCHASE_EO_QTY = new ConstantsBO("S018ONISSUINGPURCHASEEOQTY", "RO On Issueing Purchase Qty", 7);
        public static final ConstantsBO ONISSUING_PURCHASE_WO_QTY = new ConstantsBO("S018ONISSUINGPURCHASEWOQTY", "WO On Issueing Purchase Qty", 29);
        public static final ConstantsBO ONISSUING_PURCHASEHO_QTY  = new ConstantsBO("S018ONISSUINGPURCHASEHOQTY", "HO On Issueing Purchase Qty", 19);
//      public static final ConstantsBO ONTRANSIT_QTY             = new ConstantsBO("S018ONTRANSITQTY"          , "On Transit Qty",11);

        public static final ConstantsBO MANIFEST         = new ConstantsBO("S018MANIFEST", "Manifest", 12);
        public static final ConstantsBO SHIPPING_REQUEST = new ConstantsBO("S018SHIPPINGREQUEST", "Shipping Request", 13);
        public static final ConstantsBO SHIPPED          = new ConstantsBO("S018SHIPPED", "Shipped", 17);
        public static final ConstantsBO BO_QTY           = new ConstantsBO("S018BOQTY", "BackOrder Qty", 20);
        public static final ConstantsBO ALLOCATED_QTY    = new ConstantsBO("S018ALLOCATEDQTY", "Allocated Qty", 14);

        public static final ConstantsBO EO_ONPURCHASE_QTY = new ConstantsBO("S018EOONPURCHASEQTY", "EO Purchase Qty", 23);
        public static final ConstantsBO RO_ONPURCHASE_QTY = new ConstantsBO("S018ROONPURCHASEQTY", "RO Purchase Qty", 26);
        public static final ConstantsBO HO_ONPURCHASE_QTY = new ConstantsBO("S018HOONPURCHASEQTY", "HO Purchase Qty", 18);
        public static final ConstantsBO WO_ONPURCHASE_QTY = new ConstantsBO("S018WOONPURCHASEQTY", "WO Purchase Qty", 30);
    }

    public static final class ProductStockStatusType {

        public static final List<String> list = List.of(
                  "S018EOONPURCHASEQTY"
                , "S018HOONPURCHASEQTY"
                , "S018ROONPURCHASEQTY"
                , "S018WOONPURCHASEQTY");

        public static final Map<String, String> map = Map.of(
                  "S043POEO"
                , "S018EOONPURCHASEQTY"
                , "S043POHO"
                , "S018HOONPURCHASEQTY"
                , "S043PORO"
                , "S018ROONPURCHASEQTY"
                , "S043POWO"
                , "S018WOONPURCHASEQTY");
    }

    public static final class ConsumerType {

        public static final String CODE_ID = "S021";

        public static final ConstantsBO POTENTIALCUSTOMER    = new ConstantsBO("S021POTENTIALCUSTOMER", "Potential Customers", 1);
        public static final ConstantsBO BIKEPURCHASECUSTOMER = new ConstantsBO("S021BIKEPURCHASECUSTOMER", "Bike Purchase Customers", 2);
        public static final ConstantsBO TWOSCUSTOMER         = new ConstantsBO("S0212SCUSTOMER", "2S Customers", 3);
    }

    public static final class ConsumerSerialProRelationType {

        public static final String CODE_ID = "S022";

        public static final ConstantsBO OWNER = new ConstantsBO("S022OWNER", "Chủ sở hữu", 1);
        public static final ConstantsBO USER  = new ConstantsBO("S022USER", "Người dùng", 2);
    }

    public static final class DropShipType {

        public static final String CODE_ID = "S023";

        public static final String NOTDROPSHIP   = "S023NOTDROPSHIP";
        public static final String BODROPSHIP    = "S023BODROPSHIP";
        public static final String ORDERDROPSHIP = "S023ORDERDROPSHIP";
    }

    public class OrderCancelReasonTypeSub {

        public static final String CODE_ID = "S024";

        public static final String KEY_MANUALCANCEL    = "S024MANUALCANCEL";
        public static final String KEY_BOCANCEL        = "S024BOCANCEL";
        public static final String KEY_NOTSALECANCEL   = "S024NOTSALECANCEL";
        public static final String KEY_PRICEZEROCANCEL = "S024PRICEZEROCANCEL";
        public static final String KEY_COSTZEROCANCEL  = "S024COSTZEROCANCEL";
        public static final String KEY_USERDELETE      = "S024USERDELETE";
    }

    public static final class PartyRoleGroupType {

        public static final ConstantsBO COMPANY = new ConstantsBO("S025COMPANY", "COMPANY", "COMPANY", 1);
        public static final ConstantsBO DEPARTMENT = new ConstantsBO("S025DEPARTMENT", "DEPARTMENT", "DEPARTMENT", 2);
        public static final ConstantsBO CUSTOMER = new ConstantsBO("S025CUSTOMER", "KHÁCH HÀNG KHÔNG LẤY HĐ", "CUSTOMER", 3);
        public static final ConstantsBO SUPPLIER = new ConstantsBO("S025SUPPLIER", "Nhà cung cấp", "SUPPLIER", 4);
    }

    public static final class ServiceRequestStatus {

        public static final String CODE_ID = "S026";
        public static final ConstantsBO NEW = new ConstantsBO("S026NEW", "New", 0);
        public static final ConstantsBO SUBMITTED = new ConstantsBO("S026SUBMITTED", "Submitted", 1);
        public static final ConstantsBO HOLDFORREVIEW = new ConstantsBO("S026HOLDFORREVIEW", "Hold For Review", 2);
        public static final ConstantsBO APPROVED = new ConstantsBO("S026APPROVED", "Approved", 3);
        public static final ConstantsBO CONFIRMED = new ConstantsBO("S026CONFIRMED", "Confirmed", 4);
        public static final ConstantsBO HOLDINGFORPARTS = new ConstantsBO("S026HOLDINGFORPARTS", "Holding For Parts", 5);
        public static final ConstantsBO CANCELLED = new ConstantsBO("S026CANCELLED", "Cancelled", 6);
        public static final ConstantsBO DROPPED = new ConstantsBO("S026DROPPED", "Dropped", 7);
        public static final ConstantsBO ISSUED = new ConstantsBO("S026ISSUED", "Issued", 8);
        public static final ConstantsBO ERROR = new ConstantsBO("S026ERROR", "Error", 9);
        public static final ConstantsBO REJECTED = new ConstantsBO("S026REJECTED", "Rejected", 10);
        public static final ConstantsBO ACCOUNTCONFIRMED = new ConstantsBO("S026ACCOUNTCONFIRMED", "AccountConfirmed", 11);

    }

    public static final class InventoryTransactionType {

        public static final String CODE_ID = "S027";

        public static final ConstantsBO PURCHASESTOCKIN = new ConstantsBO("S027PURCHASESTOCKIN", "Purchase Stock In","IN", 1);
        public static final ConstantsBO STOCKTAKINGOUT = new ConstantsBO("S027STOCKTAKINGOUT", "Stocktaking Out", "OUT", 2);
        public static final ConstantsBO CHANGEIN = new ConstantsBO("S027CHANGEIN", "Change In","IN",3);
        public static final ConstantsBO CHANGEOUT = new ConstantsBO("S027CHANGEOUT", "Change Out","OUT", 4);
        public static final ConstantsBO BORROWINGRELEASE = new ConstantsBO("S027BORROWINGRELEASE", "Borrowing Release", 5);
        public static final ConstantsBO DISPOSAL = new ConstantsBO("S027DISPOSAL", "Disposal Out","IN", 6);
        public static final ConstantsBO EXPENSESALESTOCKOUT = new ConstantsBO("S027EXPENSESALESTOCKOUT", "Expense Sales Stock Out","OUT", 7);
        public static final ConstantsBO COSTOFBALANCE = new ConstantsBO("S027COSTOFBALANCE", "Cost of Balance", 8);
        public static final ConstantsBO BORROWINGREQUEST = new ConstantsBO("S027BORROWINGREQUEST", "Borrowing Request", 9);
        public static final ConstantsBO PURCHASERETURNOUT = new ConstantsBO("S027PURCHASERETURNOUT", "Puechase Return Out","OUT", 10);
        public static final ConstantsBO TRANSFERIN = new ConstantsBO("S027TRANSFERIN", "Transfer In","IN","OUT", 11);
        public static final ConstantsBO TRANSFEROUT = new ConstantsBO("S027TRANSFEROUT", "Transfer Out","OUT", 12);
        public static final ConstantsBO RETURNIN = new ConstantsBO("S027RETURNIN", "Return In", "IN", 13);
        public static final ConstantsBO SERVICESTOCKOUT = new ConstantsBO("S027SERVICESTOCKOUT", "Service Out","OUT", 14);
        public static final ConstantsBO SALESTOCKOUT = new ConstantsBO("S027SALESTOCKOUT", "Sales Stock Out", "OUT",15);
        public static final ConstantsBO ADJUSTIN = new ConstantsBO("S027ADJUSTIN", "Adjustment In","IN",  16);
        public static final ConstantsBO ADJUSTOUT = new ConstantsBO("S027ADJUSTOUT", "Adjustment Out", "OUT",17);
        public static final ConstantsBO BEGINNINGMONTHSTOCK = new ConstantsBO("S027BEGINNINGMONTHSTOCK", "Beginning Month Stock", 18);
        public static final ConstantsBO STOCKTAKINGIN = new ConstantsBO("S027STOCKTAKINGIN", "Stocktaking In", "IN", 19);
    }

    public static final class ServicePaymentStatus {

        public static final String CODE_ID = "S028";
        public static final ConstantsBO INFORECEIVED = new ConstantsBO("S028INFORECEIVED", "Payment Info Received", "Received", 1);
        public static final ConstantsBO CONFIRMED = new ConstantsBO("S028CONFIRMED", "Confirmed", "Confirmed", 2);
        public static final ConstantsBO CONFIRMISSUED = new ConstantsBO("S028CONFIRMISSUED", "Payment Confirm Issued", "Issued Confirm", 3);
        public static final ConstantsBO STATEMENTRECEIPT = new ConstantsBO("S028STATEMENTRECEIPT", "Statement Receipt", "Statement Confirm", 4);
    }

    public static final class ReturnReason {

        public static final String CODE_ID = "S030";
        public static final ConstantsBO UNITQUALITY = new ConstantsBO("S030UNITQUALITY", "Quality", "S001GOODS", 1);
        public static final ConstantsBO UNITDISLIKE = new ConstantsBO("S030UNITDISLIKE", "Dislike", "S001GOODS", 2);
        public static final ConstantsBO UNITEXCHANGE = new ConstantsBO("S030UNITEXCHANGE", "Exchange", "S001GOODS", 3);
        public static final ConstantsBO UNITOTHER = new ConstantsBO("S030UNITOTHER", "Other", "S001GOODS", 4);

        public static final ConstantsBO RETURNREASON001 = new ConstantsBO("S030RETURNREASON001", "1.Thông tin sai từ phía KH", "S001PART", 5);
        public static final ConstantsBO RETURNREASON002 = new ConstantsBO("S030RETURNREASON002", "2.NV bán sai PT cho khách", "S001PART", 6);
        public static final ConstantsBO RETURNREASON003 = new ConstantsBO("S030RETURNREASON003", "3.Thợ yêu cầu thông tin sai", "S001PART", 7);
        public static final ConstantsBO RETURNREASON004 = new ConstantsBO("S030RETURNREASON004", "4.Hàng đóng gói sai", "S001PART", 8);
        public static final ConstantsBO RETURNREASON005 = new ConstantsBO("S030RETURNREASON005", "5.Phụ tùng lỗi", "S001PART", 9);
        public static final ConstantsBO RETURNREASON006 = new ConstantsBO("S030RETURNREASON006", "6.Lý do khác", "S001PART", 10);
    }

    public static final class ServiceOrderStatus {

        public static final String CODE_ID = "S032";
        public static final ConstantsBO NEW = new ConstantsBO("S032NEW", "00. New", 1);
        public static final ConstantsBO WAITFORSETTLE = new ConstantsBO("S032WAITFORSETTLE", "60. Wait for settle", "", "S016UNFINISHED", 2);
        public static final ConstantsBO COMPLETED = new ConstantsBO("S032COMPLETED", "ZZ. Completed", 3);
        public static final ConstantsBO CANCELLED = new ConstantsBO("S032CANCELLED", "ZZ. Cancelled", 4);
    }


    public static final class SerialproductStockStatus {

        public static final String ONHAND         = "S033ONHAND";
        public static final String ONSHIPPING     = "S033ONSHIPPING";

        public static final String ALLOCATED      = "S033ALLOCATED";
        public static final String ONTRANSFEROUT  = "S033ONTRANSFEROUT";
        public static final String SHIPPED        = "S033SHIPPED";
        public static final String ONTRANSFERIN   = "S033ONTRANSFERIN";
        public static final String ONTRANSFER     = "S033ONTRANSIT";


    }

    public static final class SpecialClaimProblemCategory {

        public static final String CODE_ID = "S036";
        public static final String SYMPTOM = "S036SYMPTOM";
        public static final String CONDITION = "S036CONDITION";
    }

    public static final class MCSalesType {

        public static final String CODE_ID              = "S037";

        public static final String EMPLOYEESALESORDER   = "S037EMPLOYEESALESORDER";
        public static final String SPECIALSALESORDER    = "S037SPECIALSALESORDER";
        public static final String WHOLESALESORDER      = "S037WHOLESALESORDER";
        public static final String RETAILSALESORDER     = "S037RETAILSALESORDER";
    }

    public static final class InvoiceType {

        public static final String CODE_ID = "S038";
        public static final ConstantsBO SALES_INVOICE = new ConstantsBO("S038SALESINVOICE", "Sales Invoice", 1);
        public static final ConstantsBO SALES_RETURN_INVOICE = new ConstantsBO("S038SALESRETURNINVOICE", "Sales Return Invoice", 2);
    }

    public static final class SerialProQualityStatus {

        public static final String NORMAL = "S039NORMAL";
        public static final String FAULT  = "S039FAULT";

    }

    public static final class StockAdjustReasonType {

        public static final ConstantsBO LOST = new ConstantsBO("S041LOST", "Mất", 1);
        public static final ConstantsBO FINDOUT = new ConstantsBO("S041FINDOUT", "Tìm thấy", 2);
        public static final ConstantsBO DAMAGE = new ConstantsBO("S041DAMAGE", "Hàng lỗi", 3);
        public static final ConstantsBO FIXED = new ConstantsBO("S041FIXED", "Được sửa chữa", 4);
        public static final ConstantsBO STOCKTAKINGFIND = new ConstantsBO("S041STOCKTAKINGFIND", "Hàng thừa- kiểm kê", 5);
        public static final ConstantsBO STOCKTAKINGLOST = new ConstantsBO("S041STOCKTAKINGLOST", "Hàng thiếu- kiểm kê", 6);
    }

    public static final class PurchaseOrderPriorityType {

        public static final String CODE_ID = "S043";
        public static final ConstantsBO POEO = new ConstantsBO("S043POEO", "EO", 1);
        public static final ConstantsBO POHO = new ConstantsBO("S043POHO", "HO", 2);
        public static final ConstantsBO PORO = new ConstantsBO("S043PORO", "RO", 3);
        public static final ConstantsBO POWO = new ConstantsBO("S043POWO", "WO", 4);
    }

    public static final class PurchaseMethodType {

        public static final String CODE_ID = "S045";
        public static final ConstantsBO POFOQ = new ConstantsBO("S045POFOQ", "Đơn hàng theo FOQ", 1);
        public static final ConstantsBO POINDIVIDUAL = new ConstantsBO("S045POINDIVIDUAL", "Đơn hàng lẻ", 2);
        public static final ConstantsBO POPOS = new ConstantsBO("S045POPOS", "Đơn hàng trực tiếp", 3);
        public static final ConstantsBO POSERVICE = new ConstantsBO("S045POSERVICE", "Đơn hàng Service", 4);
        public static final ConstantsBO POSUPPLIER = new ConstantsBO("S045POSUPPLIER", "Supplier SQC", 5);
    }

    public static final class WarrantyPolicyType {
        public static final ConstantsBO EV = new ConstantsBO("S046EVWARRANTYPOLICY", "Ev Warranty Policy", 1);
        public static final ConstantsBO NEW = new ConstantsBO("S046NEWWARRANTYPOLICY", "New Warranty Policy", 2);
        public static final ConstantsBO OLD = new ConstantsBO("S046OLDWARRANTYPOLICY", "Old Warranty Policy", 3);
        public static final ConstantsBO BIGBIKE = new ConstantsBO("S046BIGBIKEWARRANTYPOLICY", "Big Bike Warranty Policy", 4);
        public static final ConstantsBO BATTERY = new ConstantsBO("S046BATTERYWARRANTYPOLICY", "Battery Warranty Policy", 5);
    }

    public class BaseDateType {

        public static final ConstantsBO KEY_STUDATE = new ConstantsBO("S047STUDATE", "Ngày bán", 1);
        public static final ConstantsBO KEY_LASTSERVICEDATE = new ConstantsBO("S047LASTSERVICEDATE", "Ngày thực hiện DV gần nhất", 2);
        public static final ConstantsBO KEY_SERVICESETTLEDATE = new ConstantsBO("S047SERVICESETTLEDATE", "Service Settle Date", 3);
        public static final ConstantsBO KEY_DEMANDSTARTDATE = new ConstantsBO("S047DEMANDSTARTDATE", "Demand Start Date", 4);
        public static final ConstantsBO KEY_DEMANDENDDATE = new ConstantsBO("S047DEMANDENDDATE", "Demand End Date", 5);
    }

    public static final class RegistrationDocumentFeatrueCategory {

        public static final String USETYPE = "Use Type";
        public static final String OWNERTYPE = "Owner Type";
        public static final String CODE_ID = "S048";
        public static final ConstantsBO USETYPE001 = new ConstantsBO("S048USETYPE-001", "Recreational", "Use Type", 1);
        public static final ConstantsBO USETYPE002 = new ConstantsBO("S048USETYPE-002", "Commercial", "Use Type", 2);
        public static final ConstantsBO OWNERTYPE001 = new ConstantsBO("S048OWNERTYPE-001", "INDIVIDUAL", "Owner Type", 3);
        public static final ConstantsBO OWNERTYPE002 = new ConstantsBO("S048OWNERTYPE-002", "BUSINESS", "Owner Type", 4);
        public static final ConstantsBO OWNERTYPE003 = new ConstantsBO("S048OWNERTYPE-003", "Government", "Owner Type", 5);
        public static final ConstantsBO OWNERTYPE004 = new ConstantsBO("S048OWNERTYPE-004", "Organization", "Owner Type", 6);
    }

    public static final class ReceiptSlipStatus {
        public static final String CODE_ID = "S049";
        public static final ConstantsBO RECEIPTED = new ConstantsBO("S049RECEIPTED", "Receipted", 1);
        public static final ConstantsBO STORED = new ConstantsBO("S049STORED", "Đã cất", 1);
        public static final ConstantsBO ONTRANSIT = new ConstantsBO("S049ONTRANSIT", "Đang chuyển", 1);
    }

    public static final class ManifestStatus {
        public static final String CODE_ID = "S050";
        public static final ConstantsBO WAITING_RECEIVE = new ConstantsBO("S050WAITINGRECEIVE", "", 1);
        public static final ConstantsBO WAITING_ISSUE = new ConstantsBO("S050WAITINGISSUE", "Đang chờ cấp", 1);
        public static final ConstantsBO ISSUED = new ConstantsBO("S050ISSUED", "Đã cấp", 1);
    }

    public static final class WarrantyType {
        public static final String CODE_ID = "S051";
        public static final ConstantsBO MILEAGE = new ConstantsBO("S051WARRANTYMILEAGE", "Warranty Mileage", 1);
        public static final ConstantsBO DAY = new ConstantsBO("S051WARRANTY_DAY", "Warranty Day", 2);
    }

    public static final class StocktakingRange {
        public static final String CODE_ID = "S052";
        public static final ConstantsBO MILEAGE = new ConstantsBO("S052STOCKONLY", "Stock Only Location", 1);
        public static final ConstantsBO DAY = new ConstantsBO("S052ALLLOCATION", "All Location List", 2);
    }

    public static final class StockTakingRangeType {

        public static final ConstantsBO STOCKONLY = new ConstantsBO("S083STOCKONLY", "Stock Only Location", 1);
        public static final ConstantsBO ALLLOCATION = new ConstantsBO("S083ALLLOCATION", "All Location List", 2);

    }

    public static final class RopRoqParameter {

        public static final String KEY_PARTSJ1TOTAL = "S053PARTSJ1TOTAL";
        public static final String KEY_PARTSJ2TOTAL = "S053PARTSJ2TOTAL";
        public static final String KEY_PARTSAVERAGEDEMAND = "S053PARTSAVERAGEDEMAND";
        public static final String KEY_PARTSDEVIATIONMONTH = "S053PARTSDEVIATIONMONTH";
        public static final String KEY_PARTSROPMONTH = "S053PARTSROPMONTH";
        public static final String KEY_PARTSTRENDINDEX = "S053PARTSTRENDINDEX";
        public static final String KEY_SPARTSSAFETYSTOCKMONTH = "S053PARTSSAFETYSTOCKMONTH";
        public static final String KEY_PARTSSAFETYSTOCKMONTHREAL = "S053PARTSSAFETYSTOCKMONTHREAL";
    }

    public static final class ContactMechanismType {

        public static final ConstantsBO TELEPHONE = new ConstantsBO("S056TELEPHONE", "Điện thoại", 1);
        public static final ConstantsBO FAX = new ConstantsBO("S056FAX", "Fax", 2);
        public static final ConstantsBO POSTALCODE = new ConstantsBO("S056POSTALCODE", "Mã bưu điện", 3);
    }

    public static final class ReturnRequestStatus {

        public static final String CODE_ID = "S057";
        public static final ConstantsBO RECOMMENDED = new ConstantsBO("S057RECOMMENDED", "Recommended", 1);
        public static final ConstantsBO REQUESTED = new ConstantsBO("S057REQUESTED", "Requested", 2);
        public static final ConstantsBO APPROVED = new ConstantsBO("S057APPROVED", "Approved", 3);
        public static final ConstantsBO ONPICKING = new ConstantsBO("S057ONPICKING", "On Picking", 4);
        public static final ConstantsBO COMPLETED = new ConstantsBO("S057COMPLETED", "Completed", 5);
        public static final ConstantsBO DENIED = new ConstantsBO("S057DENIED", "Denied", 6);
    }

    public static final class ReturnRequestType {

        public static final String CODE_ID = "S058";

        public static final ConstantsBO KEY_NORMAL_RETURN = new ConstantsBO("S058NORMALRETURN", "Normal Return", 1,"010");
        public static final ConstantsBO KEY_BUY_BACK = new ConstantsBO("S058BUYBACK", "Buy Back", 2, "020");
        public static final ConstantsBO KEY_SCRAP = new ConstantsBO("S058SCRAP", "Scrap", 3, "030");
    }

    public static final class SalesLeadContactStatus {

        public static final String CODE_ID = "S059";
        public static final ConstantsBO BOUGHTOTHER = new ConstantsBO("S059BOUGHTOTHER", "Đã mua xe hãng khác", "Bought Other Brand", 9);
        public static final ConstantsBO NOTBUY = new ConstantsBO("S059NOTBUY", "Không mua", "Not Buy", 8);
        public static final ConstantsBO BOUGHTYMVN = new ConstantsBO("S059BOUGHTYMVN", "Đã mua xe Yamaha", "Bought YMVN's MC", 7);
        public static final ConstantsBO WALKIN = new ConstantsBO("S059WALKIN", "Khách đến đại lý", "Walk-In", 6);
        public static final ConstantsBO COLD = new ConstantsBO("S059COLD", "Không nghe máy / không có nhu cầu", "Cold", 5);
        public static final ConstantsBO INTERESTED = new ConstantsBO("S059INTERESTED", "Quan tâm", "Interested", 4);
        public static final ConstantsBO WRONGNUMBER = new ConstantsBO("S059WRONGNUMBER", "Sai số điện thoại", "Wrong Number", 3);
        public static final ConstantsBO NOTCONNECT = new ConstantsBO("S059NOTCONNECT", "Không thể kết nối", "Not Connect", 2);
        public static final ConstantsBO CONTACT = new ConstantsBO("S059CONTACT", "--", "--", 1);
    }

    public static final class PriceStatus {

        public static final String CODE_ID = "S060";
        public static final ConstantsBO PRICEACTIVITY = new ConstantsBO("S060PRICEACTIVITY", "Hoạt động", "Activity", 1);
        public static final ConstantsBO PRICEDISABLE = new ConstantsBO("S060PRICEDISABLE", "Sắp có hiệu lực", "Pending", 2);
        public static final ConstantsBO PRICEPENDING = new ConstantsBO("S060PRICEPENDING", "Ngừng hoạt động", "Disable", 2);
    }

    public static final class PriceCategory {

        public static final String CODE_ID = "S061";
        public static final ConstantsBO GOODSEMPLOYEEPRICE = new ConstantsBO("S061GOODSEMPLOYEEPRICE", "EMPLOYEEPRICE", 1);
        public static final ConstantsBO GOODSRETAILPRICE = new ConstantsBO("S061GOODSRETAILPRICE", "RETAILPRICE", 2);
    }

    public static final class ConsumerPrivacyAgreementType {

        public static final String CODE_ID = "S062";
        public static final ConstantsBO GOODSEMPLOYEEPRICE = new ConstantsBO("C062CONSENT", "Consent", 1);
        public static final ConstantsBO GOODSRETAILPRICE = new ConstantsBO("C062PARTIALLYAGREE", "Partially Agree", 2);
    }

    public static final class UserHabitConstant {

        public static final String LANGUAGE = "S063LANGUAGE";
        public static final String PRINTPAGESIZE = "S063PRINTPAGESIZE";
        public static final String PRINTINGPAPER = "S063PRINTINGPAPER";
        public static final String UITHEME = "S063UITHEME";
        public static final String PASSWORDHISTORY = "S063PASSWORDHISTORY";
        public static final String LOGINFAILURETIMES = "S063LOGINFAILURETIMES";
        public static final String DEFAULTPOINT = "S063DEFAULTPOINT";
        public static final String LASTLOGINDATETIME = "S063LASTLOGINDATETIME";
        public static final String LOGINDATETIME = "S063LOGINDATETIME";
        public static final String QUICKMENUITEM = "S063QUICKMENUITEM";
        public static final String ISCHANGEDEALER = "S063ISCHANGEDEALER";

    }

    public static final class SalesLead {

        public static final String CODE_ID = "S064";

        public static final ConstantsBO MCSALESLEAD01 = new ConstantsBO("S064MCSALESLEAD01", "2S Sales Lead", "Bạn có thông tin khách hàng tiềm năng mới, vui lòng kiểm tra và cập nhật kết quả. ", 1);
        public static final ConstantsBO MCSALESLEAD02 = new ConstantsBO("S064MCSALESLEAD02", "MC Sales Lead", "Bạn có thông tin khách hàng tiềm năng chưa được xử lý, vui lòng kiểm tra và cập nhật kết quả. ", 2);
        public static final ConstantsBO TWOSSALESLEAD01 = new ConstantsBO("S0642SSALESLEAD01", "MC Sales Lead", "Bạn có thông tin khách hàng tiềm năng mới, vui lòng kiểm tra và cập nhật kết quả. ", 3);
    }

    public static final class RemindType {

        public static final String CODE_ID = "S065";
        public static final ConstantsBO ROUTINEINSPECTION = new ConstantsBO("S065ROUTINEINSPECTION", "Bảo trì định kỳ", "Routine inspection", 1);
        public static final ConstantsBO SERVICEFOLLOWUP = new ConstantsBO("S065SERVICEFOLLOWUP", "Theo dõi dịch vụ", "Service Follow Up", 2);
        public static final ConstantsBO SALESFOLLOWUP = new ConstantsBO("S065SALESFOLLOWUP", "Theo dõi bán hàng", "Sales Follow Up", 3);
        public static final ConstantsBO HOLIDAY = new ConstantsBO("S065HOLIDAY", "Holiday", "Holiday", 3);
    }

    public static final class Relationship {
        public static final String CODE_ID = "S066";
        public static final ConstantsBO BROTHRTSISTER = new ConstantsBO("S066BROTHRTSISTER", "Anh/Chị/em", 1);
        public static final ConstantsBO ISONDAUGHTER = new ConstantsBO("S066ISONDAUGHTER", "Con", 2);
        public static final ConstantsBO FATHERMOTHER = new ConstantsBO("S066FATHERMOTHER", "Bố/Mẹ", 3);
    }

    public static final class ReceiptManifestErrorType {

        public static final ConstantsBO POERROR = new ConstantsBO("S067POERROR", "Purchase Order Not Exist", 1);
        public static final ConstantsBO SUPPLIERERROR = new ConstantsBO("S067SUPPLIERERROR", "SUPPLIER Not Exist", 2);
        public static final ConstantsBO UNITDUPLICATEERROR = new ConstantsBO("S067UNITDUPLICATEERROR", "Unit DUPLICATE", 3);
        public static final ConstantsBO UNITSTATUSERROR = new ConstantsBO("S067UNITSTATUSERROR", "Unit was sold to user or stock in other dealer", 4);
        public static final ConstantsBO ODPARTERROR = new ConstantsBO("S067ODPARTERROR", "Order Parts Not Exist", 5);
        public static final ConstantsBO RECEIPTPARTERROR = new ConstantsBO("S067RECEIPTPARTERROR", "Receipt Parts Not Exist", 6);
        public static final ConstantsBO SUPERSEDINGERROR = new ConstantsBO("S067SUPERSEDINGERROR", "Superseding Relation Not Exist", 7);
        public static final ConstantsBO RECEIPTQTYERROR = new ConstantsBO("S067RECEIPTQTYERROR", "Receipt Qty Large than On Purchase Qty", 8);
        public static final ConstantsBO QTYERROR = new ConstantsBO("S067QTYERROR", "Qty Less Than 0", 9);
        public static final ConstantsBO RECEIPTCOSTERROR = new ConstantsBO("S067RECEIPTCOSTERROR", "Receipt Cost Less Than 0", 10);
        public static final ConstantsBO MODELERROR = new ConstantsBO("S067MODELERROR", "Model Not Exist", 11);
        public static final ConstantsBO UNITPRICEERROR = new ConstantsBO("S067UNITPRICEERROR", "Unit Price Less Than 0", 12);
    }

    public static final class UserStatusSub {

        public static final ConstantsBO ACTIVE = new ConstantsBO("S068ACTIVE", "Active", 1);
        public static final ConstantsBO INACTIVE = new ConstantsBO("S068INACTIVE", "Inactive", 2);
    }

    public static final class StockAdjustmentType {

        public static final ConstantsBO NORMALADJUSTMENT = new ConstantsBO("S069NORMALADJUSTMENT", "Điều chỉnh tồn kho (+/-)", 1);
        public static final ConstantsBO ADJUSTMENTTOFROZEN = new ConstantsBO("S069ADJUSTMENTTOFROZEN", "Điều chỉnh sang kho tạm trữ", 2);
    }

    public static class FileLoadMode {

        public static final String IMPORT = "S072IMPORT";
        public static final String EXPORT = "S072EXPORT";
        public static final String UPLOAD = "S072UPLOAD";
        public static final String DOWNLOAD = "S072DOWNLOAD";
    }

    public static final class ProductProperty {

        public static final String PARTLENGTH = "S073PARTLENGTH";
        public static final String PARTWEIGHT = "S073PARTWEIGHT";
        public static final String PARTHEIGHT = "S073PARTHEIGHT";
        public static final String PARTWIDTH = "S073PARTWIDTH";
        public static final String PARTVOLUME = "S073PARTVOLUME";
    }

    public static final class RatioType {

        public static final ConstantsBO REMINDRATIO = new ConstantsBO("S076REMINDRATIO", "Tỷ lệ nhắc nhở BT", 1);
        public static final ConstantsBO CONNECTRATIO = new ConstantsBO("S076CONNECTRATIO", "Tỷ lệ kết nối", 2);
        public static final ConstantsBO SERVICERETURNRATIO = new ConstantsBO("S076SERVICERETURNRATIO", "Tỷ lệ KH quay lại", 3);
        public static final ConstantsBO FOLLOWUPRATIO = new ConstantsBO("S076FOLLOWUPRATIO", "Tỷ lệ theo dõi dịch vụ", 4);
    }

    public static final class ContactStatus2s {

        public static final String CODE_ID = "S077";
        public static final ConstantsBO CONTACT = new ConstantsBO("S077CONTACT", "No call result yet", 1);
        public static final ConstantsBO NOTCONNECT = new ConstantsBO("S077NOTCONNECT", "Not Connect", 2);
        public static final ConstantsBO INTERESTED = new ConstantsBO("S077INTERESTED", "Interested", 3);
        public static final ConstantsBO RESERVED = new ConstantsBO("S077RESERVED", "Reserved", 4);
        public static final ConstantsBO WRONGNUMBER = new ConstantsBO("S077WRONGNUMBER", "Wrong Number", 5);
        public static final ConstantsBO COLD = new ConstantsBO("S077COLD", "Cold", 6);
        public static final ConstantsBO USESERVICE = new ConstantsBO("S077USESERVICE", "Use Service", 7);
        public static final ConstantsBO REFUSENEXTCALL = new ConstantsBO("S077REFUSENEXTCALL", "Refuse Next Call", 8);

        private static final Map<String, String> descriptionToCodeMap = new HashMap<>();
        static {
            descriptionToCodeMap.put(CONTACT.getCodeData1(), CONTACT.getCodeDbid());
            descriptionToCodeMap.put(NOTCONNECT.getCodeData1(), NOTCONNECT.getCodeDbid());
            descriptionToCodeMap.put(INTERESTED.getCodeData1(), INTERESTED.getCodeDbid());
            descriptionToCodeMap.put(RESERVED.getCodeData1(), RESERVED.getCodeDbid());
            descriptionToCodeMap.put(WRONGNUMBER.getCodeData1(), WRONGNUMBER.getCodeDbid());
            descriptionToCodeMap.put(COLD.getCodeData1(), COLD.getCodeDbid());
            descriptionToCodeMap.put(USESERVICE.getCodeData1(), USESERVICE.getCodeDbid());
            descriptionToCodeMap.put(REFUSENEXTCALL.getCodeData1(), REFUSENEXTCALL.getCodeDbid());
        }
        public static String getCodeByDescription(String description) {
            return descriptionToCodeMap.get(description);
        }
    }

    public static final class LeadStatus2s {

        public static final String CODE_ID = "S078";
        public static final ConstantsBO COLD = new ConstantsBO("S078COLD", "L1: Hot", 1);
        public static final ConstantsBO WARM = new ConstantsBO("S078WARM", "L2: Warm", 2);
        public static final ConstantsBO HOT = new ConstantsBO("S078HOT", "L3: Cold", 3);
    }

    public static final class GeographyClassification {

        public static final ConstantsBO PROVINCE = new ConstantsBO("S079PROVINCE", "Province", 1);
        public static final ConstantsBO CITY = new ConstantsBO("S079CITY", "City", 2);
    }

    public static final class PrintingPager {

        public static final ConstantsBO LETTERA4 = new ConstantsBO("S080LETTERA4", "Letter A4", 1);
        public static final ConstantsBO A4 = new ConstantsBO("S080A4", "A4", 2);
    }

    public static final class NumberPattern {

        public static final String CODE_ID = "S081";

        public static final ConstantsBO NUMBERPATTERN01 = new ConstantsBO("S081NUMBERPATTERN01", "15,0", 1);
        public static final ConstantsBO NUMBERPATTERN02 = new ConstantsBO("S081NUMBERPATTERN02", "17,1", 2);
        public static final ConstantsBO NUMBERPATTERN03 = new ConstantsBO("S081NUMBERPATTERN03", "18,2", 3);
        public static final ConstantsBO NUMBERPATTERN04 = new ConstantsBO("S081NUMBERPATTERN04", "18,3", 4);
        public static final ConstantsBO NUMBERPATTERN05 = new ConstantsBO("S081NUMBERPATTERN05", "18,4", 5);
        public static final ConstantsBO NUMBERPATTERN06 = new ConstantsBO("S081NUMBERPATTERN06", "18,5", 6);
    }

    public static final class OrderType {

        public static final String CODE_ID = "S082";

        public static final ConstantsBO SALESORDER = new ConstantsBO("S082SPSO", "Part Sales Order", "S032", 1);
        public static final ConstantsBO PURCHASEORDER = new ConstantsBO("S082SPPO", "Part Purchase Order", "S032", 2);
        public static final ConstantsBO SERVICEORDER = new ConstantsBO("S082SV", "Service Order", "S032", 3);
        public static final ConstantsBO SERVICEFOR0KMMC = new ConstantsBO("S082SVOD0KM", "Service Order(0 KM)", "S032", 4);
        public static final ConstantsBO BATTERYCLAIM = new ConstantsBO("S082SVODBATTERY", "Service Order(Battery Claim)", "S032", 5);
    }

    public static final class SdStockStatus {

        public static final String CODE_ID = "S084";

        public static final ConstantsBO ALLOCATED_QTY         = new ConstantsBO("S084ALLOCATEDQTY", "Allocated QTY", 1);
        public static final ConstantsBO ONHAND_QTY            = new ConstantsBO("S084ONHANDQTY", "On Hand QTY", 2);
        public static final ConstantsBO ONSHIPPING            = new ConstantsBO("S084ONSHIPPING", "On Shipping", 3);
        public static final ConstantsBO SHIPPED               = new ConstantsBO("S084SHIPPED", "Shipped", 4);
        public static final ConstantsBO SHIPPING_REQUEST      = new ConstantsBO("S084SHIPPINGREQUEST", "Shipping Request", 5);
        public static final ConstantsBO ONTRANSIT_QTY         = new ConstantsBO("S084ONTRANSITQTY", "On Transit QTY", 6);
        public static final ConstantsBO ONTRANSFER_IN_QTY     = new ConstantsBO("S084ONTRANSFERINQTY", "On Transfer In QTY", 7);
        public static final ConstantsBO ONTRANSFER_OUT_QTY    = new ConstantsBO("S084ONTRANSFEROUTQTY", "On Transfer Out QTY", 8);
        public static final ConstantsBO PURCHASE_STOCK_IN_QTY = new ConstantsBO("S084PURCHASESTOCKINQTY", "Purchase Stock In QTY", 9);
    }

    public static final class LocationMoveType {

        public static final String MOVEMENT_OUT = "S085MOVEMENTOUT";
        public static final String MOVEMENT_IN  = "S085MOVEMENTIN";
    }

    public static final class InterfaceStatus {

        public static final String CODE_ID = "S088";

        public static final ConstantsBO WAITINGSEND = new ConstantsBO("S088WAITINGSEND", "Waiting send", 1);
        public static final ConstantsBO SUCCESS     = new ConstantsBO("S088SUCCESS", "Success", 2);
        public static final ConstantsBO ERROR       = new ConstantsBO("S088ERROR", "Error", 3);
    }

    public static final class AnnounceType {

        public static final String CODE_ID = "S089";

        public static final String ANNOUNCE_NORMAL    = "S089NORMAL";
        public static final String ANNOUNCE_EMERGENCY = "S089EMERGENCY";
    }

    public static final class ReservationStatus {

        public static final String CODE_ID = "S090";

        public static final ConstantsBO WAITCONFIRM = new ConstantsBO("S090WAITCONFIRM", "Wait for Confirm", 1);
        public static final ConstantsBO CONFIRMED   = new ConstantsBO("S090CONFIRMED", "Confirmed", 2);
        public static final ConstantsBO CANCELLED   = new ConstantsBO("S090CANCELLED", "Cancelled", 3);
        public static final ConstantsBO COMPLETED   = new ConstantsBO("S090COMPLETED", "Completed", 4);
    }

    public static final class ReservationServiceContents {

        public static final String CODE_ID = "S091";

        public static final ConstantsBO FREEMAINTAIN = new ConstantsBO("S091FREEMAINTAIN", "Free Maintain", 1);
        public static final ConstantsBO PAIDMAINTAIN = new ConstantsBO("S091PAIDMAINTAIN", "Paid Maintain/Repair", 2);
        public static final ConstantsBO OILCHANGE    = new ConstantsBO("S091OILCHANGE", "Oil change/ Replace Fast moving part", 3);
        public static final ConstantsBO SMALLREPAIR  = new ConstantsBO("S091SMALLREPAIR", "Small Repair", 4);
    }

    public static final class ReservationMethod {

        public static final String CODE_ID = "S092";

        public static final ConstantsBO DMS         = new ConstantsBO("S092DMS", "DMS", 1);
        public static final ConstantsBO MYYAMAHAAPP = new ConstantsBO("S092MYYAMAHAAPP", "My Yamaha APP", 2);
    }

    public static final class CostUsage {

        public static final String CODE_ID = "S093";

        public static final ConstantsBO COSTUSAGEA1 = new ConstantsBO("COSTUSAGEA1", "A1", "80", "145000", "99999999", 1);
        public static final ConstantsBO COSTUSAGEA2 = new ConstantsBO("COSTUSAGEA2", "A2", "80", "30000", "145000", 2);
        public static final ConstantsBO COSTUSAGEA3 = new ConstantsBO("COSTUSAGEA3", "A3", "80", "0", "30000", 3);
        public static final ConstantsBO COSTUSAGEB1 = new ConstantsBO("COSTUSAGEB1", "B1", "90", "145000", "99999999", 4);
        public static final ConstantsBO COSTUSAGEB2 = new ConstantsBO("COSTUSAGEB2", "B2", "90", "30000", "145000", 5);
        public static final ConstantsBO COSTUSAGEB3 = new ConstantsBO("COSTUSAGEB3", "B3", "90", "0", "30000", 6);
        public static final ConstantsBO COSTUSAGEC1 = new ConstantsBO("COSTUSAGEC1", "C1", "100", "145000", "99999999", 7);
        public static final ConstantsBO COSTUSAGEC2 = new ConstantsBO("COSTUSAGEC2", "C2", "100", "30000", "145000", 8);
        public static final ConstantsBO COSTUSAGEC3 = new ConstantsBO("COSTUSAGEC3", "C3", "100", "0", "30000", 9);
        public static final ConstantsBO COSTUSAGED1 = new ConstantsBO("COSTUSAGED1", "D1", "0", "145000", "99999999", 10);
        public static final ConstantsBO COSTUSAGED2 = new ConstantsBO("COSTUSAGED2", "D2", "0", "30000", "145000", 11);
        public static final ConstantsBO COSTUSAGED3 = new ConstantsBO("COSTUSAGED3", "D3", "0", "0", "30000", 12);
        public static final ConstantsBO COSTUSAGEE1 = new ConstantsBO("COSTUSAGEE1", "E1", "0", "145000", "99999999", 13);
        public static final ConstantsBO COSTUSAGEE2 = new ConstantsBO("COSTUSAGEE2", "E2", "0", "30000", "145000", 14);
        public static final ConstantsBO COSTUSAGEE3 = new ConstantsBO("COSTUSAGEE3", "E3", "0", "0", "30000", 15);
        public static final ConstantsBO COSTUSAGEN1 = new ConstantsBO("COSTUSAGEN1", "N1", "0", "145000", "99999999", 16);
        public static final ConstantsBO COSTUSAGEN2 = new ConstantsBO("COSTUSAGEN2", "N2", "0", "30000", "145000", 17);
        public static final ConstantsBO COSTUSAGEN3 = new ConstantsBO("COSTUSAGEN3", "N3", "0", "0", "30000", 18);
        public static final ConstantsBO COSTUSAGEXX = new ConstantsBO("COSTUSAGEXX", "XX", "0", "0", "0", 19);
        public static final ConstantsBO COSTUSAGEYY = new ConstantsBO("COSTUSAGEYY", "YY", "0", "0", "0", 20);
    }

    public static final class ReservationTime {

        public static final String CODE_ID = "S094";

        public static final ConstantsBO HOUR08TO09 = new ConstantsBO("S094HOUR08TO09", "08:00 ~ 09:00", 1);
        public static final ConstantsBO HOUR09TO10 = new ConstantsBO("S094HOUR09TO10", "09:00 ~ 10:00", 2);
        public static final ConstantsBO HOUR10TO11 = new ConstantsBO("S094HOUR10TO11", "10:00 ~ 11:00", 3);
        public static final ConstantsBO HOUR11TO12 = new ConstantsBO("S094HOUR11TO12", "11:00 ~ 12:00", 4);
        public static final ConstantsBO HOUR13TO14 = new ConstantsBO("S094HOUR13TO14", "13:00 ~ 14:00", 5);
        public static final ConstantsBO HOUR14TO15 = new ConstantsBO("S094HOUR14TO15", "14:00 ~ 15:00", 6);
        public static final ConstantsBO HOUR15TO16 = new ConstantsBO("S094HOUR15TO16", "15:00 ~ 16:00", 7);
        public static final ConstantsBO HOUR16TO17 = new ConstantsBO("S094HOUR16TO17", "16:00 ~ 17:00", 8);
    }

    public static final class BatteryOriginalSource {

        public static final String CODE_ID = "S095";

        public static final String ORIGINAL  = "S095ORIGINAL";
        public static final String PARTSALES = "S095PARTSALES";
        public static final String SERVICE   = "S095SERVICE";
    }

    public static final class CrmApiType{

        public static final String CODE_ID = "S096";

        public static final ConstantsBO DMSSDMANIFEST  = new ConstantsBO("S096DMSSDMANIFEST","",1);
    }

    public static final class QueueStatus {

        public static final String CODE_ID = "S097";

        public static final ConstantsBO WAITINGSEND = new ConstantsBO("S097WAITINGSEND","Waiting send", 1);
        public static final ConstantsBO SUCCESS     = new ConstantsBO("S097SUCCESS",  "Success", 2);
        public static final ConstantsBO ERROR       = new ConstantsBO("S097ERROR", "Error", 3);
    }

    public static final class MessageCategoryType {

        public static final String INFORMATIONREADY = "S098INFORMATIONREADY";//OneReaderClose
        public static final String REPORTREADY = "S098REPORTREADY";//AfterProcessClose
    }

    public static final class PurchaseType {

        public static final String CODE_ID = "S099";

        public static final ConstantsBO INCREASE = new ConstantsBO("S099INCREASE", "Increase", 1);

        public static final ConstantsBO REPLACE  = new ConstantsBO("S099REPLACE", "Replace", 2);

        public static final ConstantsBO NEW      = new ConstantsBO("S099NEW", "New", 3);
    }

    public static final class ManifestItemStatus {

        public static final String CODE_ID = "S100";
        public static final ConstantsBO WAITING_RECEIVE = new ConstantsBO("S100WAITINGRECEIVE", "WAITINGRECEIVE", 1);
        public static final ConstantsBO RECEIVING       = new ConstantsBO("S100RECEIVING", "RECEIVING", 1);
        public static final ConstantsBO RECEIVED        = new ConstantsBO("S100RECEIVED", "RECEIVED", 1);
    }

    public static final class ReadType {

        public static final String READ   = "read";
        public static final String UNREAD = "unread";

        private ReadType() {
            throw new AssertionError("No instances for you!");
        }
    }

    public static final class StockTakingTypeVietnamese {

        public static final String SYSTEM_TOTAL      = "Tổng trên hệ thống";
        public static final String ACTUAL_TOTAL      = "Tổng thực tế";
        public static final String QTYEQUAL          = "Số lượng thực tế = số lượng trên hệ thống";
        public static final String QTYEXCEED         = "Số lượng thực tế > số lượng trên hệ thống";
        public static final String QTYLACK           = "Số lượng thực tế < số lượng trên hệ thống";
        public static final String ACCURACY_PERCCENT = "Tỉ lệ chính xác";
    }

    public static final class MessageType {

        public static final String HOMEPAGE = "HOMEPAGE";
        public static final String YPEC = "YPEC";

        private MessageType() {
            throw new AssertionError("No instances for you!");
        }
    }

    public static final class NoticeType {

        public static final String MESSAGE = "Message";

        private NoticeType() {
            throw new AssertionError("No instances for you!");
        }
    }


}