package com.a1stream.common.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class BaseManager {

    /*
     * protected final static String getValue(String fieldName, Object entity) {
     *
     * return StringUtils.toString(getFieldValueByName(fieldName, entity)); }
     */

    private static Object getFieldValueByName(String fieldName, Object screenEntity) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = screenEntity.getClass().getMethod(getter);
            return method.invoke(screenEntity);
        } catch (Exception e) {
            log.error("getFieldValueByName error!", e);
            return null;
        }
    }

    protected final static void setValue(Object dbEntity, Object screenEntity, Set<String> fieldNameSet) {

        Field[] intersectionFields;

        if (fieldNameSet.size() > 0) {
            intersectionFields = getParameterFields(dbEntity, fieldNameSet);
        } else {
            intersectionFields = getIntersectionFields(dbEntity, screenEntity);
        }

        for (Field dbField : intersectionFields) {

            Object value = getFieldValueByName(dbField.getName(), screenEntity);

            setFieldValueByName(dbField.getName(), dbEntity, value, dbField.getType());
        }
    }

    private static Field[] getParameterFields(Object dbEntity, Set<String> fieldName) {

        Field[] dbFields     = dbEntity.getClass().getDeclaredFields();

        Field[] result = new Field[fieldName.size()];
        int resultIndex = 0;

        for (Field dbField : dbFields) {
            if (fieldName.contains(dbField.getName())) result[resultIndex++] = dbField;
        }

        return result;

    }

    private static Field[] getIntersectionFields(Object dbEntity, Object screenEntity) {

        Field[] dbFields     = dbEntity.getClass().getDeclaredFields();
        Field[] screenFields = screenEntity.getClass().getDeclaredFields();

        Set<String> dbFieldNameSet = new HashSet<>();
        Set<String> intersectionFieldNameSet = new HashSet<>();

        for (Field dbField : dbFields) {
            dbFieldNameSet.add(dbField.getName());
        }

        for (Field screenField : screenFields) {
            if (dbFieldNameSet.contains(screenField.getName())) intersectionFieldNameSet.add(screenField.getName());
        }

        Field[] result = new Field[intersectionFieldNameSet.size()];
        int resultIndex = 0;
        for (Field dbField : dbFields) {
            if (intersectionFieldNameSet.contains(dbField.getName())) result[resultIndex++] = dbField;
        }

        return result;
    }

    private static void setFieldValueByName(String fieldName, Object executeMember, Object value, Class<?> clazz) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setter = "set" + firstLetter + fieldName.substring(1);
            Method method = executeMember.getClass().getMethod(setter, clazz);
            method.invoke(executeMember, value);
        } catch (Exception e) {
            log.error("setFieldValueByName error!", e);
            //e.printStackTrace();
        }
    }
}
