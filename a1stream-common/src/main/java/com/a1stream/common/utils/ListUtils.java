package com.a1stream.common.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    /**
     * 按指定大小，分隔集合，将集合按规定的个数分为n个部分
     *
     * @param <T>
     * @param list
     *            list列表
     * @param len
     *            长度
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }

        List<List<T>> result = new ArrayList<List<T>>();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }
}
