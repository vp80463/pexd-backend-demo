package com.a1stream.common.constants;

import com.a1stream.common.model.ConstantsBO;

public class MstCodeConstants {

    public static class YmvnSite {
        public static final String S075YMVNSITE                     = "YT00";
    }

    public static class FacilityRoleType{
        public static final String KEY_SHOP                         = "S003SHOP";
    }

    public static class SalesOrderStatus {

        public static final String CODE_ID                          = "S015";
        /**
         * GOODS
         */
        public static final String PROFORMA                         = "S015PROFORMA";
        public static final String WAITING_ALLOCATE                 = "S015WAITINGALLOCATE";
        public static final String WAITING_SHIPPING                 = "S015WAITINGSHIPPING";
        public static final String INSTRUCTION                      = "S015INSTRUCTION";
        public static final String SHIPPED                          = "S015SHIPPED";
        public static final String CANCELLED                        = "S015CANCELLED";
        public static final String ONSHIPPING                       = "S015ONSHIPPING";
        /**
         * PART
         */
        public static final String SP_CREATED                       = "S015SPCREATED";
        public static final String SP_PROFORMA                      = "S015SPPROFORMA";
        public static final String SP_WAITINGFORRELEASE             = "S015SPWAITINGFORRELEASE";
        public static final String SP_WAITINGFORAPPROVE             = "S015SPWAITINGFORAPPROVE";
        public static final String SP_WAITINGALLOCATE               = "S015SPWAITINGALLOCATE";
        public static final String SP_WAITINGPICKING                = "S015SPWAITINGPICKING";
        public static final String SP_ONPICKING                     = "S015SPONPICKING";
        public static final String SP_SHIPMENTED                    = "S015SPSHIPMENTED";
        public static final String SP_CANCELLED                     = "S015SPCANCELLED";
    }

    public static final class DeliveryStatus {

        public static final String CODE_ID                          = "S029";
        public static final String SHIPPING_REQUEST                 = "S029SHIPPINGREQUEST";
        public static final String SHIPPING_INSTRUCTION             = "S029SHIPPINGINSTRUCTION";
        public static final String SHIPPING_COMPLETION              = "S029SHIPPINGCOMPLETION";
        public static final String ON_PICKING                       = "S029ONPICKING";
        public static final String INVOICED                         = "S029INVOICED";
        public static final String DISPATCHED                       = "S029DISPATCHED";
        public static final String CREATED                          = "S029CREATED";

        public static final String PACKING_COMPLETE                 = "S029PACKINGCOMPLETE";
        public static final String ON_PACKING                       = "S029ONPACKING";
    }

    public static final class PaymentMethodType {

        public static final String CODE_ID                          = "S031";
        public static final String CASH                             = "S031CASH";
    }

    public static final class ServiceOrderStatus {

        public static final String CODE_ID                          = "S032";
        public static final String NEW                              = "S032NEW";
        public static final String WAIT_FOR_SETTLE                  = "S032WAITFORSETTLE";
        public static final String COMPLETED                        = "S032COMPLETED";
        public static final String CANCELLED                        = "S032CANCELLED";
    }

    public static final class SerialproductStockStatus {

        public static final String SHIPPED = "S033SHIPPED";
    }

    public static final class FacilityMultiAddress {

        public static final String CODE_ID                          = "S035";
    }

    public static final class PurchaseOrderStatus {

        public static final String CODE_ID                          = "S042";
        public static final String SPWAITINGISSUE                   = "S042SPWAITINGISSUE";
        public static final String SPONPURCHASE                     = "S042SPONPURCHASE";
        public static final String SPONRECEIVING                    = "S042SPONRECEIVING";
        public static final String SPREGISTERED                     = "S042SPREGISTERED";
        public static final String SPCANCELLED                      = "S042SPCANCELLED";
    }

    public static final class ManifestStatus {
        public static final String CODE_ID = "S050";

        public static final ConstantsBO WAITING_ISSUE = new ConstantsBO("S050WAITINGISSUE", "Đang chờ cấp", 1);
        public static final ConstantsBO ISSUED = new ConstantsBO("S050ISSUED", "Đang chuyển", 1);
    }

    public static class PartsSaftyFactor {

        public static final String CODE_ID                          = "S054";
    }

    public static class PartsSafetyFactory {

        public static final String CODE_ID                          = "S054";
    }

    public static class JudgementStatus {

        public static final String CODE_ID                          = "S055";
        public static final String WAITINGUPLOAD                    = "S055WAITINGUPLOAD";
        public static final String WAITINGYMVNCHECKING              = "S055WAITINGYMVNCHECKING";
        public static final String APPROVEDBYSD                     = "S055APPROVEDBYSD";
        public static final String REJECTBYSD                       = "S055REJECTBYSD";
        public static final String APPROVEDBYYMVN                   = "S055APPROVEDBYYMVN";
        public static final String REJECTBYACCOUNT                  = "S055REJECTBYACCOUNT";

    }


    public static class FacilityRelationType{
        public static final String KEY_DELIVERYINCHARGE             = "S071DELIVERYINCHARGE";
    }

    public static class SystemParameterType {

        public static final String CODE_ID                          = "S074";
        public static final String LASTDAILYBATCH                   = "S074LASTDAILYBATCH";
        public static final String GROWTHLOWER                      = "S074GROWTHLOWER";
        public static final String GROWTHUPPER                      = "S074GROWTHUPPER";
        public static final String STOCKTAKING                      = "S074STOCKTAKING";
        public static final String DEADSTOCKMONTHS                  = "S074DEADSTOCKMONTHS";
        public static final String TAXPERIOD                        = "S074TAXPERIOD";
        public static final String INITIALBATCHFLAG                 = "S074INITIALBATCHFLAG";
        public static final String LASTMONTHLYBATCH                 = "S074LASTMONTHLYBATCH";
        public static final String CURRENTFINANCEMONTH              = "S074CURRENTFINANCEMONTH";
        public static final String LASTMICOLLECTDATE                = "S074LASTMICOLLECTDATE";
        public static final String BATCHPROCESSFLAG                 = "S074BATCHPROCESSFLAG";
        public static final String JOBTAXRATE                       = "S074JOBTAXRATE";
        public static final String STDWORKINGHOURPRICECUSTOMER      = "S074STDWORKINGHOURPRICECUSTOMER";
        public static final String STDWORKINGHOURPRICEFACTORY       = "S074STDWORKINGHOURPRICEFACTORY";
        public static final String TAXRATE                          = "S074TAXRATE";
        public static final String AUTOPOFLAG                       = "S074AUTOPOFLAG";
        public static final String COGNITOPOOL                      = "S074COGNITOPOOL";
        public static final String STOCKTAKINGDISPLAYFLAG           = "S074STOCKTAKINGDISPLAYFLAG";
        public static final String POPROGRESSPSW                    = "S074POPROGRESSPSW";
        public static final String YNSPRESPSYSOWNERCD               = "S074YNSPRESPSYSOWNERCD";
        public static final String WEBSOCKETPORT                    = "S074WEBSOCKETPORT";
        public static final String GETFILEBASEURL                   = "S074GETFILEBASEURL";
        public static final String PRIVACYPOLICYPHOTOPATH           = "S074PRIVACYPOLICYPHOTOPATH";

        public static final String ACCESS_END_TIME                  = "S074ACCESSENDTIME";
        public static final String ACCESS_START_TIME                = "S074ACCESSSTARTTIME";

        public static final String YMVNSTOCKPSW                     = "S074YMVNSTOCKPSW";
        public static final String PROMOTION_BATCH_URL              = "S074PROMOTIONBATCHURL";
        public static final String PROMOTION_XML_ROOT_DIR           = "S074PROMOTIONXMLROOTDIR";

        public static final String PROMOTION_PICTURE_URL            = "S074PROMOTIONPICTUREURL";
        public static final String PROMOTION_PICTURE_URL_INTERNAL   = "S074PROMOTIONPICTUREURLINTERNAL";
        
        public static final String LAST_PSI_DATE                    = "S074LASTPSIDATE";


    }

    public class InterfaceCodeTypeSub{
        public static final String KEY_YNSPIRESPEO                              = "C165YNSPIRESPEO";
        public static final String KEY_EINVOICE_MC_RETAIL                       = "C165EINVOICEMCRETAIL";
        public static final String KEY_EINVOICE_PART_SALES                      = "C165EINVOICEPARTSALES";
        public static final String KEY_EINVOICE_SERVICE                         = "C165EINVOICESERVICE";
        public static final String KEY_YPCMMSVHISUPD                            = "C165YPCMMSVHISUPD";
        public static final String KEY_YPCMMSVHISDEL                            = "C165YPCMMSVHISDEL";
        public static final String KEY_YPCMMSVHISITEMUPD                        = "C165YPCMMSVHISITEMUPD";
        public static final String KEY_YPCMMSVHISITEMDEL                        = "C165YPCMMSVHISITEMDEL";
        public static final String KEY_YPPRODUCTUPD                             = "C165YPPRODUCTUPD";
        public static final String KEY_YPVINTELUPD                              = "C165YPVINTELUPD";
        public static final String KEY_YPVINTELDEL                              = "C165YPVINTELDEL";
        public static final String KEY_MCINVENTORY                              = "C165MCINVENTORY";
        public static final String KEY_PARTMANIFEST                             = "C165PARTMANIFEST";
        public static final String KEY_FCSJUDGEMENT                             = "C165FCSJUDGEMENT";
        public static final String KEY_CRMRECALLMC                              = "C165CRMRECALLMC";
        public static final String KEY_CRMRECALLMCMODIFY                        = "C165CRMRECALLMCMODIFY";
        public static final String KEY_SVWARRANTYORDER                          = "C165SVWARRANTYORDER";
        public static final String KEY_PARTSSALESORDER                          = "C165PARTSSALESORDER";
        public static final String KEY_AUTHNO                                   = "C165AUTHNO";
        public static final String KEY_DMSCUSINFORMATION                        = "C165DMSCUSINFORMATION";
        public static final String KEY_OWNERSHIP                                = "C165OWNERSHIP";
        public static final String KEY_SVRECALLORDER                            = "C165SVRECALLORDER";
        public static final String KEY_0KMSVRECALLORDER                         = "C1650KMSVRECALLORDER";
        public static final String KEY_SVFCSORDER                               = "C165SVFCSORDER";
        public static final String KEY_0KMSVWARRANTYORDER                       = "0kmsvwarrantyorder";
        public static final String KEY_CUSINFORMATION                           = "C165DMSCUSINFORMATION";
    }

    public class McSalesStatus {

        public static final String CODE_ID                          = "S040";
        public static final String STOCK                            = "S040STOCK";
        public static final String SALESTOUSER                      = "S040SALESTOUSER";
    }

    public static final class Occupation {

        public static final String CODE_ID                          = "S019";
        public static final String NA                               = "S019NA";
    }
}
