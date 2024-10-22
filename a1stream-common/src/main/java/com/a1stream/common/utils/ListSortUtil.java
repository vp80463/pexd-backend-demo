package com.a1stream.common.utils;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ListSortUtil {

    public static final String SORT_ASC = "asc";

    public static final String SORT_DESC = "desc";

    public static List<BigDecimal> sortBigDecimalList(List<BigDecimal> list, final String sort) {

        Collections.sort(list, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {

                int ret = 0;

                ret = String.valueOf(o1).compareTo(String.valueOf(o2));

                if (null != sort && SORT_DESC.equalsIgnoreCase(sort)) {
                    return -ret;
                } else {
                    return ret;
                }
            }
        });

        return list;
    }

    /**
     * マルチカラムソートを行います。
     *
     * @param  list     ソート対象リスト
     * @param  fields   フィールド配列
     * @return ソート済みリスト
     * @throws IllegalArgumentException 引数が不正な場合
     */
    public static ArrayList sort(ArrayList list, String[] fields) throws IllegalArgumentException {

        if (fields == null) {
            throw new IllegalArgumentException("fields is null.");
        }

        return sort(list, fields, new boolean[fields.length]);
    }

    /**
     * マルチカラムソートを行います。
     *
     * @param  list     ソート対象リスト
     * @param  fields   フィールド配列
     * @return ソート済みリスト
     * @throws IllegalArgumentException 引数が不正な場合
     */
    public static List sort(List list, String[] fields) throws IllegalArgumentException {

        if (fields == null) {
            throw new IllegalArgumentException("fields is null.");
        }

        return sort(list, fields, new boolean[fields.length]);
    }


    /**
     * マルチカラムソートを行います。
     *
     * @param  list     ソート対象リスト
     * @param  fields   フィールド配列
     * @param  reverses 逆順配列
     * @return ソート済みリスト
     * @throws IllegalArgumentException 引数が不正な場合
     */
    public static ArrayList sort(ArrayList list, String[] fields, boolean[] reverses) throws IllegalArgumentException {
        return (ArrayList)sort((List)list, fields, reverses);
    }

    /**
     * マルチカラムソートを行います。
     *
     * @param  list     ソート対象リスト
     * @param  fields   フィールド配列
     * @param  reverses 逆順配列
     * @return ソート済みリスト
     * @throws IllegalArgumentException 引数が不正な場合
     */
    public static List sort(List list, String[] fields, boolean[] reverses) throws IllegalArgumentException {

        if (list == null || list.size() == 0) {
            return list;
        }

        if (fields == null) {
            throw new IllegalArgumentException("fields is null.");
        }

        if (reverses == null) {
            throw new IllegalArgumentException("reverses is null.");
        }

        if (fields.length != reverses.length) {
            throw new IllegalArgumentException("The size of fields and reverses is not in agreement.");
        }

        if (fields.length == 0) {
            return list;
        }

        // マルチカラムソート用のコンパレータ
        ComparatorChain cc = new ComparatorChain();

        for (int i = 0; fields != null && i < fields.length; i++) {

            String  field   = fields[i];
            boolean reverse = reverses[i];

            if (field.trim().length() == 0) {
                continue;
            }

            cc.addComparator(
                     new BeanComparator(
                        field,
                        new NullComparator(ComparableComparator.getInstance())),
                    reverse);
        }

        if (cc.size() != 0) {
            // ソート
            Collections.sort(list, cc);
        }

        return list;
    }
}

