package com.a1stream.common.constants;

import java.util.Arrays;
import java.util.List;

public class SeqConstants {

    public static final Integer INTEGER_ZERO               = 0;

    public static final String FORMULA_ELEMENT_DELIMITER   = "\\|";

    public static final String FORMULA_SITE                = "site";
    public static final String FORMULA_FACILITY            = "facNoId";
    public static final String FORMULA_SEQ_NO              = "seqNo";

    public static final String FORMULA_YEAR                = "yy";
    public static final String FORMULA_YEAR_MONTH          = "yymm";
    public static final String FORMULA_YEAR_MONTH_DAY      = "yymmdd";
    public static final String FORMULA_ALL_YEAR            = "yyyy";
    public static final String FORMULA_ALL_YEAR_MONTH      = "yyyymm";
    public static final String FORMULA_ALL_YEAR_MONTH_DAY  = "yyyymmdd";

    public final static List<String> FORMULA_DATE_TYPE = Arrays.asList(FORMULA_YEAR
                                                                        , FORMULA_YEAR_MONTH
                                                                        , FORMULA_YEAR_MONTH_DAY
                                                                        , FORMULA_ALL_YEAR
                                                                        , FORMULA_ALL_YEAR_MONTH
                                                                        , FORMULA_ALL_YEAR_MONTH_DAY);

    public static final class SeqNoType {

        public static final String FAST_SHIPPING_SO                 = "NONSERIAL0001";
        public static final String NONSERIAL_SALESORDERNO           = "NONSERIAL0002";
        public static final String DELIVERY_NO                      = "NONSERIAL0004";
        public static final String PICKING_LIST_NO                  = "NONSERIAL0005";
        public static final String NONSERIAL_STORINGLINENO          = "NONSERIAL0016";
        public static final String INVOICE_NO                       = "NONSERIAL0007";
        public static final String LOCATION_TRANS_NO                = "NONSERIAL0008";
        public static final String NONSERIAL_PURCHASEORDERNO        = "NONSERIAL0009";
        public static final String SLIP_NO                          = "NONSERIAL0011";
        public static final String NONSERIAL_STORINGLISTNO          = "NONSERIAL0012";
        public static final String RETURN_INVOICENO                 = "NONSERIAL0015";
        public static final String BARRERY_SERVICEORDERNO           = "NONSERIAL0031";

        public static final String SERIAL_SERVICEORDERNO            = "SERIAL0001";
        public static final String SERIAL_SALESORDERNO              = "SERIAL0002";
        public static final String SERIAL_DELIVERYBATCHNO           = "SERIAL0003";
        public static final String SERIAL_DELIVERYORDERNO           = "SERIAL0004";
        public static final String SERIAL_INVOICENO                 = "SERIAL0007";
        public static final String SERIAL_SALESPRICELISTNO          = "SERIAL0010";
        public static final String SERIAL_RECEIPTSLIPNO             = "SERIAL0011";
        public static final String SERIAL_STORINGLISTNO             = "SERIAL0012";
        public static final String SERIAL_RETURN_INVOICENO          = "SERIAL0015";
        public static final String SERIAL_PROMOTIONCODE             = "SERIAL0016";

        public static final String SERVICE_SERVICEREQUESTNO         = "SERVICE0004";
    }
}
