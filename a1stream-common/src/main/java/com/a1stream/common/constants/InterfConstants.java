package com.a1stream.common.constants;

public class InterfConstants {

    public static class InterfCode{

        //Test
        public static final String YMVNDMS_ASYNCTEST                   = "ymvnDms_AsyncTest";
        public static final String A1STREAM_YMVNDMS_ASYNCAPITEST       = "a1stream_YmvnDms_AsyncApiTest";
        public static final String A1STREAM_YMVNDMS_SYNCTEST           = "a1stream_YmvnDms_SyncTest";
        public static final String A1STREAM_YMVNDMS_SYNCGETTEST        = "a1stream_YmvnDms_SyncGetTest";
        public static final String A1STREAM_YMVNDMS_ASYNCSQSTEST       = "a1stream_YmvnDms_ASyncSQSTest";

        //Call webservice.
        public final static String DMSTOSP_PARTSSTOCK_INQ              = "DmsToSp_PartsStock_INQ";
        public final static String DMSTOSP_POPROGRESS_INQ              = "DmsToYnspireSp_PoProgress";
        public final static String DMSTOSP_SENDPO_INQ                  = "DmsToSp_SendPO_INQ";
        public final static String DMSTOSP_SPMANIFEST_INQ              = "DmsToSp_SPManifest_INQ";
        public final static String DMSTOSP_RECEIVEREPORT_INQ           = "DmsToSp_ReceiveReport_INQ";
        public final static String DMSTOSP_SQCANSWER                   = "DmsToSp_SqcAnswer";
        public final static String DMS_TO_YNSPIRESP_SPRECOMMENDATION_RETURN = "DMS_TO_YNSPIRESP_SPRECOMMENDATION_RETURN";
        public final static String DMS_TO_YNSPIRESW_REGISTRATIONDOCUMENT = "YNSW.WS.YNSW_REGISTRATIONDOCUMENT";

        //Interface
        public static final String OX_SPDEADSTOCK                      = "DmsToSp_spDeadStock";
        public static final String IMPORTTODMS_PARTSINFO               = "SpToDms_PartsInfo";
        public static final String IX_SPRECOMMENDATIONRETURN           = "SpToDms_spRecommendationReturn";
        public static final String IX_SPPURCHASECANCEL                 = "SpToDms_PurchaseCancel";
        public static final String OX_SPRETURNREQUEST                  = "DmsToSp_spReturnRequest";
        public static final String OX_SPPURCHASEORDER                  = "DmsToSp_sppurchaseorder";
        public static final String IX_SPRETURNAPPROVAL                 = "SpToDms_spReturnApproval";
        public static final String IX_SPQUOTATION                      = "SpToDMS_SpQuotation";
        public static final String SPMANIFEST                          = "SpToDms_Manifest";
        public static final String OX_SPSTORINGREPORT                  = "DmsToSp_StoringReport";
        public static final String IX_SPDISCOUNTRATE                   = "SpToDms_PurchaseDis";
        public static final String IX_PARTSTAX                         = "SpToDms_PartsTax";
        public static final String IX_SPCREDIT                         = "SpToDms_SpCredit";
        public static final String IX_SPWHOLESALESIGN                  = "SpToDms_SpWholeSaleSign";

        public static final String DMS_TO_DMS_SV_SERVICESETTLE         = "DmsToDms_ServiceSettle";
        public static final String OX_SV_COUPONREQ                     = "DmsToSW_SvCouponReq";
        public static final String OX_SV_CLAIMREQ                      = "DmsToSW_SvClaimReq";
        public static final String OX_SV_PAYMENTCONFIRM                = "DmsToSW_SvPaymentConfirm";

        public static final String IX_SV_AUTHORIZATIONNO               = "SwToDms_SvAuthorizationNo";
        public static final String IX_SV_BIGBIKEMODELINFO              = "SwToDms_SvBigBikeModelInfo";
        public static final String IX_SV_CLAIMJUDGERESULT              = "SwToDms_SvClaimJudgeResult";
        public static final String IX_SV_COUPONJUDGERESULT             = "SwToDms_SvCouponJudgeResult";
        public static final String IX_SV_JOBFLATRATE                   = "SwToDms_SvJobFlatrate";
        public static final String IX_SV_JOBMASTER                     = "SwToDms_SvJobMaster";
        public static final String IX_SV_MODIFIEDSPECIALCLAIMRESULT    = "SwToDms_SvModifiedSpecialClaimResult";
        public static final String IX_SV_PAYMENT                       = "SwToDms_SvPayment";
        public static final String IX_SV_REGISTERDOC                   = "SwToDms_SvRegisterDoc";
        public static final String IX_SV_REGISTERDOCCHANGE             = "SwToDms_SvRegisterDocChange";
        public static final String IX_SV_SPECIALCLAIMAPPLICATION       = "SwToDms_SvSpecialClaimApplication";
        public static final String IX_SV_SPECIALCLAIMMASTER            = "SwToDms_SvSpecialClaimMaster";
        public static final String IX_SV_SPECIALCLAIMTARGETMC          = "SwToDms_SvSpecialClaimTargetMc";
        public static final String IX_SV_WARRANTY                      = "SwToDms_SvWarranty";
        public static final String IX_SV_WARRANTY_PARTS                = "SwToDms_SvWarrantyParts";

        public static final String DMS_TO_MYY_SV_SERVICEHISTORY        = "DmsToMYY_SvServiceHistory";
        public static final String DMS_TO_MYY_SV_SERVICEHISTORYITEM    = "DmsToMYY_SvServiceHistoryItem";
        public static final String DMS_TO_MYY_SV_VINCODETEL            = "DmsToMYY_SvVinCodeTel";

        public static final String IMPORTTODMS_SDPRODUCTINFO           = "SdToDms_SdProductInfo";
        public static final String SD_TO_DMS_SDMANIFEST                = "SdToDms_SdManifest";
        public static final String DMS_TO_SD_SDPSIEXPORT               = "DmsToSd_SdPSIExport";

        public static final String XM03_INTERF_O_SV_REG_DOC            = "OX_svRegisterDoc";
        public static final String XM03_INTERF_O_SV_REG_DOC_FOR_BATTERY= "OX_svRegisterDocForBattery";
    }
}
