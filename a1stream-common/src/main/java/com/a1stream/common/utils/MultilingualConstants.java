package com.a1stream.common.utils;

import java.util.HashMap;
import java.util.Map;

public class MultilingualConstants {

    public static final Map<String, String> ZhConstants = new HashMap<String, String>() {
        private static final long serialVersionUID = 4953774015290783766L;
        {
            put("ME.00001", "错误");
        }
    };

    public static final Map<String, String> EngConstants = new HashMap<String, String>() {
        private static final long serialVersionUID = 4953774015290783766L;
        {
            put("ME.00001", "error");
        }
    };

    public static final Map<String, String> JpConstants = new HashMap<String, String>() {
        private static final long serialVersionUID = 4953774015290783766L;
        {
            put("ME.00001", "エラ-");
        }
    };
}
