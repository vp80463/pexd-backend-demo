package com.a1stream.common.constants;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class CommonConstants {

    public static final String AUDIT_DFAULT_FUNC_ID                         = "---";
    public static final String CHAR_IFS                                     = "IFS";
    public static final String SH_FACILITY_CODE                             = "SHCK";
    public static final String CQ_FACILITY_CODE                             = "CQCK";
    public static final String CHAR_SUPPER_ADMIN_FLAG                       = "Y";
    public static final String CHAR_DEFAULT_SYSTEM                          = "PJ";
    public static final String CHAR_API_USER_ID                             = "API";
    public static final String CHAR_BATCH_USER_ID                           = "BATCH";
    public static final String CHAR_YAMAHA                                  = "YAMAHA";
    public static final String CHAR_ADMIN                                   = "ADMIN";
    public static final String CHAR_DEFAULTPHONE                            = "19012345678";
    public static final String CHAR_ALL                                     = "ALL";
    public static final String CHAR_OTHER                                   = "OTHER";

    public static final String CHAR_CMMDB                                   = "/cmm";

    public static final String MI_DATASOURCE_ID_KEY                         ="a1stream-mi-db";
    public static final String CHAR_DEFAULT_SITE_ID                         = "666N";
    public static final String CHAR_YMSLX_SITE_ID                           = "YMSLX";
    public static final String CHAR_DEFAULT_SITE_ID_HO                      = "YAMAHA";
    public static final String CHAR_DEFAULT_SITE_ID_DO                      = "YMVNDO";
    public static final String CHAR_SITE_ID_RY03                            = "RY03";
    public static final String CHAR_YT00_SITE_ID                            = "YT00";
    public static final String CHAR_DMS                                     = "DMS";

    public static final String CHAR_QUANTITY                                = "Quantity";
    public static final String CHAR_QTY                                     = "Qty";


    public static final String CHAR_BLANK                                   = "";
    public static final String CHAR_SPACE                                   = " ";
    public static final String CHAR_DOT                                     = ".";
    public static final String CHARACTER_DOT                                = "\\.";
    public static final String CHAR_NEW_LINE                                = "\n";
    public static final String CHAR_COMMA                                   = ",";
    public static final String CHAR_COLON                                   = ":";
    public static final String CHAR_SEMICOLON                               = ";";
    public static final String CHAR_BAR                                     = "-";
    public static final String CHAR_UNDERSCORE                              = "_";
    public static final String CHAR_DOUBLE_UNDERSCORE                       = "__";
    public static final String CHAR_ASTERISK                                = "*";
    public static final String CHAR_PERCENT                                 = "%";
    public static final String CHAR_SLASH                                   = "/";
    public static final String CHAR_QUOTES                                  = "'";
    public static final String CHAR_DOUBLE_QUOTES                           = "\"";
    public static final String CHAR_TRUE                                    = "true";
    public static final String CHAR_FALSE                                   = "false";
    public static final String CHAR_VERTICAL_BAR                            = "|";
    public static final String CHAR_DOUBLE_VERTICAL_BAR                     = "||";
    public static final String CHARACTER_VERTICAL_BAR                       = "\\|";
    public static final String PRICE_SIGN                                   = "￥";
    public static final String CHAR_ADDZEROFORMAT                           = "%02d";

    public static final String CHAR_AND                                     = "&";

    public static final String CHAR_EQUAL                                   = "=";
    public static final String CHAR_NEGATIVE_ONE                            = "-1";
    public static final String CHAR_ZERO                                    = "0";
    public static final String CHAR_ONE                                     = "1";
    public static final String CHAR_TWO                                     = "2";
    public static final String CHAR_THREE                                   = "3";
    public static final String CHAR_FOUR                                    = "4";
    public static final String CHAR_FIVE                                    = "5";
    public static final String CHAR_SIX                                     = "6";
    public static final String CHAR_SEVEN                                   = "7";
    public static final String CHAR_EIGHT                                   = "8";
    public static final String CHAR_NINE                                    = "9";
    public static final String CHAR_TEN                                     = "10";
    public static final String CHAR_ELEVEN                                  = "11";
    public static final String CHAR_TWELVE                                  = "12";

    public static final String CHAR_HUNDRED                                 = "100";
    public static final String CHAR_THOUSAND                                = "1000";
    public static final String CHAR_MILLION                                 = "1000000";
    public static final String CHAR_EIGHT_ZERO                              = "00000000";
    public static final String CHAR_OPTION_CODE                             = "010";
    public static final String CHAR_010101                                  = "010101";
    public static final String CHAR_ONE_THOUSAND_AND_TWO_HUNDRED            = "1200";

    public static final Integer INTEGER_NEGATIVE_ONE                        = -1;
    public static final Integer INTEGER_ZERO                                = 0;
    public static final Integer INTEGER_ONE                                 = 1;
    public static final Integer INTEGER_TWO                                 = 2;
    public static final Integer INTEGER_THREE                               = 3;
    public static final Integer INTEGER_FOUR                                = 4;
    public static final Integer INTEGER_FIVE                                = 5;
    public static final Integer INTEGER_SIX                                 = 6;
    public static final Integer INTEGER_SEVEN                               = 7;
    public static final Integer INTEGER_EIGHT                               = 8;
    public static final Integer INTEGER_NINE                                = 9;
    public static final Integer INTEGER_TEN                                 = 10;
    public static final Integer INTEGER_ELEVEN                              = 11;
    public static final Integer INTEGER_TWELVE                              = 12;
    public static final Integer INTEGER_THIRTEEN                            = 13;
    public static final Integer INTEGER_FOURTEEN                            = 14;
    public static final Integer INTEGER_FIFTEEN                             = 15;
    public static final Integer INTEGER_SIXTEEN                             = 16;
    public static final Integer INTEGER_SEVENTEEN                           = 17;
    public static final Integer INTEGER_EIGHTEEN                            = 18;
    public static final Integer INTEGER_NINETEEN                            = 19;
    public static final Integer INTEGER_TWENTY                              = 20;
    public static final Integer INTEGER_TWENTY_ONE                          = 21;
    public static final Integer INTEGER_TWENTY_TWO                          = 22;
    public static final Integer INTEGER_TWENTY_THREE                        = 23;
    public static final Integer INTEGER_TWENTY_FOUR                         = 24;
    public static final Integer INTEGER_THIRTY                              = 30;
    public static final Integer INTEGER_HUNDRED                             = 100;
    public static final Integer INTEGER_TWO_HUNDRED_NINETY_EIGHT            = 298;
    
    public static final BigDecimal BIGDECIMAL_ZERO                          = new BigDecimal("0");
    public static final BigDecimal BIGDECIMAL_ZERO_ROUND1                   = new BigDecimal("0.0");
    public static final BigDecimal BIGDECIMAL_ZERO_ROUND2                   = new BigDecimal("0.00");
    public static final BigDecimal BIGDECIMAL_ZERO_ROUND4                   = new BigDecimal("0.0000");
    public static final BigDecimal BIGDECIMAL_ONE                           = new BigDecimal("1");
    public static final BigDecimal BIGDECIMAL_ONE_PERSENT                   = new BigDecimal("0.01");
    public static final BigDecimal BIGDECIMAL_ONE_THOUSAND                  = new BigDecimal("0.0001");
    public static final BigDecimal BIGDECIMAL_HUNDRED_ROUND1                = new BigDecimal("100.0");
    public static final BigDecimal BIGDECIMAL_HUNDRED_ROUND2                = new BigDecimal("100.00");
    public static final BigDecimal BIGDECIMAL_HUNDRED_ROUND3                = new BigDecimal("1000.00");
    public static final BigDecimal BIGDECIMAL_MAX                           = new BigDecimal("9999");
    public static final BigDecimal BIGDECIMAL_TWELVE                        = new BigDecimal("12");
    public static final BigDecimal BIG_DECIMAL_THIRTY                       = new BigDecimal("30");

    public static final String DB_DATE_FORMAT_YMD                           = "yyyyMMdd";
    public static final String DB_DATE_FORMAT_YM                            = "yyyyMM";
    public static final String DB_DATE_FORMAT_YYMM                          = "yyMM";
    public static final String DB_DATE_FORMAT_YMDHM                         = "yyyyMMddHHmm";
    public static final String DB_DATE_FORMAT_Y                             = "yyyy";
    public static final String DB_DATE_FORMAT_M                             = "MM";
    public static final String DB_DATE_FORMAT_D                             = "dd";
    public static final String DB_DATE_FORMAT_MDHM                          = "MM-dd HH:mm";
    public static final String DB_DATE_FORMAT_HM                            = "HHmm";
    public static final String DB_DATE_FORMAT_H_M                           = "HH:mm";
    public static final String DB_DATE_FORMAT_H_M_S                         = "HH:mm:ss";
    public static final String DB_DATE_FORMAT_HMS                           = "HHmmss";
    public static final String DB_DATE_FORMAT_H                             = "HH";
    public static final String DB_DATE_FORMAT_YMDHMS                        = "yyyyMMddHHmmss";
    public static final String DEFAULT_SIMPLEDATE_FORMAT                    = "dd/MM/yyyy HH:mm:ss.SSS";
    public static final String DEFAULT_SIMPLEDATE_YMDHMS                    = "dd/MM/yyyy HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT                          = "dd/MM/yyyy";
    public static final DateTimeFormatter DEFAULT_SIMPLEDATE_YMDHMS_LOC     = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter DEFAULT_DATE_FORMAT_LOC           = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final String DEFAULT_DATE_DETAIL_FORMAT                   = "yyyyMMddHHmmssSSS";
    public static final String DEFAULT_SECOND_FORMAT_WITH_COLON             = ":00.000";
    public static final String DEFAULT_SECOND_FORMAT                        = "00";
    public static final String MIN_TIME                                     = "00:00:00";
    public static final String MAX_TIME                                     = "23:59:59";
    public static final String MAX_DATE                                     = "20991231";
    public static final String MIN_DATE                                     = "19700101";
    public static final String EMAIL_REG_FORMAT                             = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w-]+\\.)+[\\w]+[\\._\\w]*[\\w]$";

    public static final String OPERATION_STATUS_NEW                         = "NEW";
    public static final String OPERATION_STATUS_UPDATE                      = "UPDATE";
    public static final String OPERATION_STATUS_CONFIRM                     = "CONFIRM";
    public static final String OPERATION_STATUS_CANCEL                      = "CANCEL";
    public static final String OPERATION_STATUS_DELETE                      = "DELETE";
    public static final String OPERATION_STATUS_FINISH                      = "FINISH";
    public static final String FLAG_DELETEED                                = "1";
    public static final String FLAG_UNDELETE                                = "0";
    public static final String FLAG_SEND                                    = "1";
    public static final String FLAG_UNSEND                                  = "0";
    public static final String FLAG_READ                                    = "1";
    public static final String FLAG_UNREAD                                  = "0";
    public static final String FLAG_ACTIVE                                  = "1";
    public static final String FLAG_UNACTIVE                                = "0";

    public static final String CHAR_YES                                     = "YES";
    public static final String CHAR_NO                                      = "NO";
    public static final String CHAR_Y                                       = "Y";
    public static final String CHAR_N                                       = "N";
    public static final String CHAR_SMALL_N                                 = "n";
    public static final String CHAR_C                                       = "C";
    public static final String CHAR_LUB                                     = "LUB";
    public static final String CHAR_L                                       = "L";
    public static final String CHAR_S                                       = "S";

    public static final String CHAR_H                                       = "H";
    public static final String CHAR_M                                       = "M";

    public static final Boolean TRUE_CODE                                   = true;
    public static final Boolean FALSE_CODE                                  = false;

    public static final String CHAR_WEEK                                    = "WEEK";
    public static final String CHAR_MONTH                                   = "MONTH";
    public static final String DEFAULT_RESERVE_TIME                         = "0000";

    public static final String WEEK_MONDAY_ENG                              = "Monday";
    public static final String WEEK_TUESDAY_ENG                             = "Tuesday";
    public static final String WEEK_WEDNESDAY_ENG                           = "Wednesday";
    public static final String WEEK_THURSDAY_ENG                            = "Thursday";
    public static final String WEEK_FRIDAY_ENG                              = "Friday";
    public static final String WEEK_SATURDAY_ENG                            = "Saturday";
    public static final String WEEK_SUNDAY_ENG                              = "Sunday";

    public static final String GROUP_TYPE_DAY                               = "day";
    public static final String GROUP_TYPE_WEEK                              = "week";
    public static final String GROUP_TYPE_MONTH                             = "month";
    public static final String GROUP_TYPE_YEAR                              = "year";

    public static final String CHAR_SHEET_SUMMARY       = "Summary";
    public static final String CHAR_SHEET_DETAIL        = "Detail";

    public static final String LANGUAGE_ZH              = "zh";
    public static final String LANGUAGE_EN              = "en";
    public static final String LANGUAGE_PJ              = "pj";

    /******************************************************/
    /* Currency Fractional Part Scale                     */
    /******************************************************/
    public static final int PRICE_ROUND_ZERO                                = 0;
    public static final int PRICE_FRAC_SCALE                                = 2;
    public static final int COST_FRAC_SCALE                                 = 4;
    public static final int PAGE_SIZE                                       = 20;
    public static final int JDBC_BATCH_SIZE                                 = 50;
    public static final int DAYS_FOR_HALF_A_YEAR                            = 184;
    public static final int DAYS_FOR_ONE_YEAR                               = 365;
    public static final int DAYS_FOR_ONE_QUARTER                            = 93;

    //monthly batch
    public static final Integer DB_OP_BATCH_SIZE                    = 20000;
    public static final String ROPQBATCH_UPDATE_PROGRAM             = "ROPQBATCH";
    public static final String CHAR_A                               = "A";
    public static final String CHAR_B                               = "B";
    public static final String CHAR_D                               = "D";
    public static final String CHAR_E                               = "E";
    public static final String CHAR_R                               = "R";
    public static final String CHAR_W                               = "W";
    public static final String CHAR_XX                              = "XX";
    public static final String CHAR_YY                              = "YY";
    public static final String STRING_EIGHT_ZERO                    = "0.00000000";
    public static final int PRODUCT_TYPE_MODEL                      = 0;
    public static final int PRODUCT_TYPE_PARTS                      = 1;
    public static final BigDecimal BIGDECIMAL_UP                    = new BigDecimal("0.49999999");

    public static final String CHAR_ADD                              = "add";
    public static final String CHAR_EDIT                             = "edit";
    public static final String CHAR_PLUS                             = "PLUS";
    public static final String CHAR_MINUS                            = "MINUS";


    public static final class WarrantyClaimDefaultPara {

        public static final BigDecimal OLD_WARRANTY_MILEAGE     = new BigDecimal(12000);
        public static final BigDecimal OLD_WARRANTY_TERM        = new BigDecimal(12);
        public static final BigDecimal NEW_WARRANTY_MILEAGE     = new BigDecimal(30000);
        public static final BigDecimal NEW_WARRANTY_TERM        = new BigDecimal(36);
        public static final BigDecimal BIGBIKE_WARRANTY_MILEAGE = new BigDecimal(1000);
        public static final BigDecimal BIGBIKE_WARRANTY_TERM    = new BigDecimal(12);
        public static final BigDecimal EV_WARRANTY_TERM         = new BigDecimal(24);
    }

    public static final String CHAR_BOXONE                                  = "BOX1";
    public static final int INT_SIX                                         = 6;
    public static final String CHAR_LEFT_PARENTHESIS                        = "(";
    public static final String CHAR_RIGHT_PARENTHESIS                       = ")";

    public static final String AVAILABLE   = "Available";

    public static final String SALES_TO_CUSTOMER                            = "SALES_TO_CUSTOMER";

    public static final String INSERT                                       = "INSERT";
    public static final String AUTO                                         = "AUTO";
    public static final String CMMSERIALIZEDPRODUCTINFO                     = "cmm_serialized_product_info";

    public static final String CHAR_INVOICE                                 = "Invoice";
    public static final String CHAR_TAXAUTHORITY                            = "TaxAuthority";
    public static final String END_DATE                                     = "99991231";
}
