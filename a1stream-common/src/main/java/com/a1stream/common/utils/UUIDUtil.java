package com.a1stream.common.utils;

import java.util.UUID;

public class UUIDUtil {

    public static String getUUID() {
        //最大支持1-9个集群机器部署
        int machineId = 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        //有可能是负数
        if(hashCodeV < 0) {
            hashCodeV = - hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return machineId + String.format("%018d", hashCodeV);
    }

    public static String getSmsCode() {
        //每次调用生成一位四位数的随机数
        Integer code = (int)(Math.random()*9999)+1;
        String result = Integer.toString(code);

        if(result.length()!=4) {
            return getSmsCode();
        }

        return result;
    }
}
