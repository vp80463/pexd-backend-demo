package com.a1stream.common.constants;

/**
 * @author dong zhen
 */
public final class RedisFormatConstants {

    private RedisFormatConstants() {
        // 防止实例化
        throw new AssertionError("No instances for you!");
    }

    public static final String DATASOURCE = "dataSource:%s";

    public static final String SMS_VERIFICATION_CODE = "sms:%s:%s";

    public static final String USER_MENU_COUNT = "usermenu:count:%s:%s";
}
