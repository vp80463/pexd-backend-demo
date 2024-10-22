/******************************************************************************/
/* SYSTEM     : Commons                                                       */
/*                                                                            */
/* SUBSYSTEM  : Util                                                          */
/******************************************************************************/
package com.a1stream.common.utils;

/**
 * 零件编号用的实用程序类。
 */
public class PartNoUtil {

    /**
     * 非常规部件编号
     */
    public static final String[] IRREGULAR_PART_NO = { "PART NOT EX." };

    protected PartNoUtil() {}

    /**
     * 编辑部件编号的格式
     *
     * @param str 零件编号
     * @return 编辑格式后的部件编号
     */
    public static String format(String str) {

        if ( str == null ) {
            return null;
        }

        if ( str.trim().length() == 0 ) {
            return "";
        }

        if ( str.trim().length() == 10 ) {
            str = str.trim() + "00";
        }

        if ( str.length() != 12 ) {
            return str;
        }

        for ( int i = 0; i < IRREGULAR_PART_NO.length; i++ ) {
            if ( str.equals( IRREGULAR_PART_NO[i] ) ) {
                // 非常规部件编号
                return str;
            }
        }

        if ( str.substring( 0, 1 ).equals( "9" ) ) {

            if ( str.substring( 10, 12 ).equals( "00" ) ) {
                // 5-5
                return str.substring( 0, 5 ) + "-" + str.substring( 5, 10 );

            } else {
                // 5-5-2
                return str.substring( 0, 5 ) + "-" + str.substring( 5, 10 ) + "-" + str.substring( 10, 12 );
            }

//        } else if ( str.substring( 0, 1 ).equals( "Q" ) ) {
//            // 3-3-3-3
//            return str.substring( 0, 3 ) + "-" + str.substring( 3, 6 ) + "-" + str.substring( 6, 9 ) + "-" + str.substring( 9, 12 );
//
//        } else if ( str.substring( 0, 1 ).equals( "I" ) ) {
//            // 3-3-6
//            return str.substring( 0, 3 ) + "-" + str.substring( 3, 6 ) + "-" + str.substring( 6, 12 );

        } else {

            if ( str.substring( 10, 12 ).equals( "00" ) ) {
                // 3-5-2
                return str.substring( 0, 3 ) + "-" + str.substring( 3, 8 ) + "-" + str.substring( 8, 10 );

            } else {
                // 3-5-2-2
                return str.substring( 0, 3 ) + "-" + str.substring( 3, 8 ) + "-" + str.substring( 8, 10 ) + "-" + str.substring( 10, 12 );
            }
        }
    }

    /**
     * 将部件编号转换为DB保持用。
     *
     * @param  partNo 部品编号
     * @return 转换后的部件编号
     */
    public static String formaForDB(String partNo) {

        if (partNo == null) {
            return null;
        }

        partNo = partNo.trim();

        for (int i = 0; i < IRREGULAR_PART_NO.length; i++) {
            if (partNo.equals(IRREGULAR_PART_NO[i])) {
                // 非常规部件编号
                return partNo;
            }
        }

        // 删除“-”
        while (true) {
            int idx = partNo.indexOf("-");
            if (idx == -1) {
                break;
            }
            partNo = partNo.substring(0, idx) + partNo.substring(idx + "-".length());
        }

        if (partNo.length() == 10) {
            // 10位的情况下在末尾添加“00”
            partNo += "00";
        }

        return partNo;
    }
}
