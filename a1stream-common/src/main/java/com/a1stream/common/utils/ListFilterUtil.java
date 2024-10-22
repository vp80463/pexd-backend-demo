package com.a1stream.common.utils;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.base.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public final class ListFilterUtil {

    public static String GET_MODEL_KEY = "MODEL_OBJECT_KEY";
    public static String GET_COUNT_RESULT_KEY = "COUNT_RESUT_KEY";
    public static String GET_SUM_RESULT_KEY = "SUM_RESUT_KEY_";
    public static String GET_MODEL_LIST_KEY  = "GET_MODEL_LIST_KEY";

    public static String GET_FILTER_MATCH_LIST_KEY  = "GET_FILTER_MATCH_LIST_KEY";
    public static String GET_FILTER_UN_MATCH_LIST_KEY  = "GET_FILTER_UN_MATCH_LIST_KEY";



    /**
     * filter the same content is list(only support for String)
     * @param list
     * @return
     */
    public static List<String> filterSameContent(List<String> list){

        List<String> returnList = new ArrayList<String>();
        HashMap hm = new HashMap();

        for(String nodeStr:list){
            if(!hm.containsKey(nodeStr)){
                returnList.add(nodeStr);
                hm.put(nodeStr, null);
            }
        }
        return returnList;
    }


    /**  b
     * filterBy a list by some condition
     * <pre>
     *  * e.x
     *   ListFilterUtil.filterBy(list, new String[]{"id","name","close"}, new String[]{">=","=",">"}, new Object[]{"0001",1,false});
     * </pre>
     * @param list
     * @param fieldNames : filed must has getter method is model.
      *                       e.x.  "xyz" filed must has getXyz() method
      *                          "isCBU" filed must has getIsCBU method
      * {@code operationName:   (=)    (!=)   (>)  (>=)   (<)   (<=)}
     * @param operationName:  compare value
     * @return GroupListResult
     *        you can used  GroupListResult.getFilterMatchList() method get matched list;
     *        or GroupListResult.getFilterUnMatchList() method get unmached list;
     *
     */

    public static GroupListResult filterBy(List list,String[] fieldNames,String[] operationName,Object[] values){
        GroupListResult result = new GroupListResult();

        Iterator it = list.iterator();
        String fieldName;
        Object fieldValue;
        List matchList = new ArrayList();
        List unMatchList = new ArrayList();
        while (it.hasNext()) {
            boolean isMacth = false;
            Object model = it.next();

            for(int i=0;i<fieldNames.length;i++){
                fieldName = fieldNames[i];
                fieldValue = getModelValue(model, changeVariable2GetMethod(fieldName));

                if(  (!operationName[i].equals("="))
                  && (!operationName[i].equals("!="))
                  && (!operationName[i].equals(">"))
                  && (!operationName[i].equals(">="))
                  && (!operationName[i].equals("<"))
                  && (!operationName[i].equals("<="))
                        ){
                    throw new RuntimeException("Operation type:[" + operationName[i] + "] is Invalid!");
                }

                if (fieldValue instanceof Integer) {
                    isMacth = compare((Integer)fieldValue,operationName[i],(Integer)values[i]);
                } else if (fieldValue instanceof Double) {
                    isMacth = compare((Double)fieldValue,operationName[i],(Double)values[i]);
                } else if (fieldValue instanceof String) {
                    isMacth = compare((String)fieldValue,operationName[i],(String)values[i]);
                } else if (fieldValue instanceof Float) {
                    isMacth = compare((Float)fieldValue,operationName[i],(Float)values[i]);
                } else if (fieldValue instanceof Long) {
                    isMacth = compare((Long)fieldValue,operationName[i],(Long)values[i]);
                } else if (fieldValue instanceof BigDecimal) {
                    isMacth = compare((BigDecimal)fieldValue,operationName[i],(BigDecimal)values[i]);
                }else if (fieldValue instanceof Boolean) {
                    isMacth = compare((Boolean)fieldValue,operationName[i],(Boolean)values[i]);
                }else{
                    throw new RuntimeException("This type:[" + fieldValue.getClass() + "] can't be support");
                }

                if(!isMacth){
                    break;
                }

            }

            if(isMacth)
                matchList.add(model);
            else
                unMatchList.add(model);
        }
        result.put(ListFilterUtil.GET_FILTER_MATCH_LIST_KEY, matchList);
        result.put(ListFilterUtil.GET_FILTER_UN_MATCH_LIST_KEY, unMatchList);

        return result;
    }

    /**
     * filterBy a list by some condition
     * <pre>
     *   e.x
     *   ListFilterUtil.filterBy(list, new String[]{"id","name","close"}, new String[]{">=","=",">"}, new Object[]{"0001",1,false});
     * </pre>
     * @param list
     * @param fieldNames : filed must has getter method is model.
      *                       e.x.  "xyz" filed must has getXyz() method
      *                          "isCBU" filed must has getIsCBU method
      * {@code operationName:   (=)    (!=)   (>)  (>=)   (<)   (<=)}
     * @param operationName:  compare value
     * @param valueNullAble: true
     * @return GroupListResult
     *        you can used  GroupListResult.getFilterMatchList() method get matched list;
     *        or GroupListResult.getFilterUnMatchList() method get unmached list;
     */

    public static GroupListResult filterBy(List list,String[] fieldNames,String[] operationName,Object[] values,Boolean valueNullAble){
        GroupListResult result = new GroupListResult();

        Iterator it = list.iterator();
        String fieldName;
        Object fieldValue;
        List matchList = new ArrayList();
        List unMatchList = new ArrayList();
        while (it.hasNext()) {
            boolean isMacth = false;
            Object model = it.next();

            for(int i=0;i<fieldNames.length;i++){
                fieldName = fieldNames[i];
                fieldValue = getModelValue(model, changeVariable2GetMethod(fieldName));

                if(  (!operationName[i].equals("="))
                  && (!operationName[i].equals("!="))
                  && (!operationName[i].equals(">"))
                  && (!operationName[i].equals(">="))
                  && (!operationName[i].equals("<"))
                  && (!operationName[i].equals("<="))
                        ){
                    throw new RuntimeException("Operation type:[" + operationName[i] + "] is Invalid!");
                }
                if((Nulls.isNullOrEmpty(fieldValue) || Nulls.isNullOrEmpty(values[i]))&& valueNullAble) {
                    isMacth = Nulls.isNullOrEmpty(fieldValue) == Nulls.isNullOrEmpty(values[i]);
                }else if (fieldValue instanceof Integer) {
                    isMacth = compare((Integer)fieldValue,operationName[i],(Integer)values[i]);
                } else if (fieldValue instanceof Double) {
                    isMacth = compare((Double)fieldValue,operationName[i],(Double)values[i]);
                } else if (fieldValue instanceof String) {
                    isMacth = compare((String)fieldValue,operationName[i],(String)values[i]);
                } else if (fieldValue instanceof Float) {
                    isMacth = compare((Float)fieldValue,operationName[i],(Float)values[i]);
                } else if (fieldValue instanceof Long) {
                    isMacth = compare((Long)fieldValue,operationName[i],(Long)values[i]);
                } else if (fieldValue instanceof BigDecimal) {
                    isMacth = compare((BigDecimal)fieldValue,operationName[i],(BigDecimal)values[i]);
                }else if (fieldValue instanceof Boolean) {
                    isMacth = compare((Boolean)fieldValue,operationName[i],(Boolean)values[i]);
                }else{
                    throw new RuntimeException("This type:[" + fieldValue.getClass() + "] can't be support");
                }

                if(!isMacth){
                    break;
                }

            }

            if(isMacth)
                matchList.add(model);
            else
                unMatchList.add(model);
        }
        result.put(ListFilterUtil.GET_FILTER_MATCH_LIST_KEY, matchList);
        result.put(ListFilterUtil.GET_FILTER_UN_MATCH_LIST_KEY, unMatchList);

        return result;
    }

    /**
     * group by some field ,and count record number in list
     * @param list
     * @param fieldNames : filed must has getter method is model.
      *                    e.x.  "xyz" filed must has getXyz() method
      *                          "isCBU" filed must has getIsCBU method
     *
     * @return
     */
    public static List countGroupBy(List list,String[] fieldNames){
        List resultList = new ArrayList();
        List key;

        HashMap hm = new HashMap();
        Iterator it = list.iterator();
        GroupListResult hmValue;
        List modelList;

        while (it.hasNext()) {
            Object model = it.next();
            key = generateKey(model,fieldNames);

            if (!hm.containsKey(key)) {
                modelList = new ArrayList();
                modelList.add(model);

                hmValue = new GroupListResult();

                fillHaspMapValue(hmValue,key,fieldNames);

                hmValue.put(ListFilterUtil.GET_COUNT_RESULT_KEY, 1);
                hmValue.put(ListFilterUtil.GET_MODEL_KEY, model);
                hmValue.put(ListFilterUtil.GET_MODEL_LIST_KEY, modelList);

                hm.put(key, hmValue);
                resultList.add(hmValue);
            } else {
                hmValue = (GroupListResult)hm.get(key);
                modelList = (ArrayList)hmValue.get(ListFilterUtil.GET_MODEL_LIST_KEY);

                Integer countNum = (Integer)hmValue.get(ListFilterUtil.GET_COUNT_RESULT_KEY);
                countNum ++;

                hmValue.put(ListFilterUtil.GET_COUNT_RESULT_KEY, countNum);
                modelList.add(model);
                hm.put(key, hmValue);
            }
        }

        return resultList;
    }

    /**
    *
    * @param list List
    * @param fieldNames String[]
    * @param sumFiledNames String[]
    */

    public static List sumGroupBy(List list,String[] fieldNames,String[] sumFiledNames){

        List resultList = new ArrayList();
        List key;

        HashMap hm = new HashMap();
        Iterator it = list.iterator();
        GroupListResult hmValue;
        List modelList;

        while (it.hasNext()) {
            Object model = it.next();
            key = generateKey(model,fieldNames);

            if (!hm.containsKey(key)) {
                modelList = new ArrayList();
                modelList.add(model);

                hmValue = new GroupListResult();

                fillHaspMapValue(hmValue,key,fieldNames);

                for(int i=0;i<sumFiledNames.length;i++){
                    Object nowValue = getModelValue(model, changeVariable2GetMethod(sumFiledNames[i]));
                    hmValue.put(ListFilterUtil.GET_SUM_RESULT_KEY + sumFiledNames[i], nowValue);
                }

                hmValue.put(ListFilterUtil.GET_COUNT_RESULT_KEY, 1);
                hmValue.put(ListFilterUtil.GET_MODEL_KEY, model);
                hmValue.put(ListFilterUtil.GET_MODEL_LIST_KEY, modelList);

                hm.put(key, hmValue);
                resultList.add(hmValue);
            } else {
                hmValue = (GroupListResult)hm.get(key);

                //set sumary plus
                for(int i=0;i<sumFiledNames.length;i++){
                    Object originValue = hmValue.get(ListFilterUtil.GET_SUM_RESULT_KEY + sumFiledNames[i]);
                    Object nowValue = getModelValue(model, changeVariable2GetMethod(sumFiledNames[i]));
                    Object newValue = null;

                    if (originValue instanceof Integer) {
                        newValue = (Integer)originValue  + (Integer)nowValue;
                    } else if (originValue instanceof Double) {
                        newValue = (Double)originValue  + (Double)nowValue;
                    } else if (originValue instanceof Float) {
                        newValue = (Float)originValue  + (Float)nowValue;
                    } else if (originValue instanceof Long) {
                        newValue = (Long)originValue  + (Long)nowValue;
                    } else if (originValue instanceof BigDecimal) {
                        newValue = ((BigDecimal)originValue).add((BigDecimal)nowValue);
                    }else{
                        throw new RuntimeException("This type:[" + originValue.getClass() + "] can't be sumary");
                    }
                    hmValue.put(ListFilterUtil.GET_SUM_RESULT_KEY + sumFiledNames[i], newValue);
                }

                //set count plus
                Integer countNum = (Integer)hmValue.get(ListFilterUtil.GET_COUNT_RESULT_KEY);
                countNum ++;
                hmValue.put(ListFilterUtil.GET_COUNT_RESULT_KEY, countNum);

                modelList = (ArrayList)hmValue.get(ListFilterUtil.GET_MODEL_LIST_KEY);
                modelList.add(model);

                hm.put(key, hmValue);
            }
        }

        return resultList;

    }

    private static List generateKey(Object model,String[] fieldNames){
        List key = new ArrayList();
        String fieldName;
        Object fieldValue;

        for(int i=0;i<fieldNames.length;i++){
            fieldName = fieldNames[i];
            fieldValue = getModelValue(model, changeVariable2GetMethod(fieldName));
            key.add(fieldValue);
        }
        return key;
    }

    private static void fillHaspMapValue(HashMap hm,List key,String[] fieldNames){
        String fieldName;
        Object fieldValue;

        for(int i=0;i<fieldNames.length;i++){
            fieldName = fieldNames[i];
            fieldValue = key.get(i);
            hm.put(fieldName, fieldValue);
        }
    }

    private static String changeVariable2GetMethod(String variableStr){
        return "get" + variableStr.substring(0,1).toUpperCase() + variableStr.substring(1);
    }

    private static Object getModelValue(Object model,String methodName){
        Method m ;
        Object reObj = null ;
        try{
            m = model.getClass().getMethod(methodName, new Class[]{});
            reObj = m.invoke(model, new Object[]{});
        }catch(SecurityException e){
            throw new RuntimeException(e.toString());
        }
        catch(NoSuchMethodException e){
            throw new RuntimeException(e.toString());
        }
         catch(IllegalArgumentException e){
             throw new RuntimeException(e.toString());
         }
         catch(IllegalAccessException e){
             throw new RuntimeException(e.toString());
         }
         catch(InvocationTargetException e){
             throw new RuntimeException(e.toString());
         }

         return reObj;
    }

    public static Object getModelValueByFieldName(Object model,String fieldName) {
        return getModelValue(model, changeVariable2GetMethod(fieldName));
    }

    /*compare String*/
    private static boolean compare(String v1,String operStr,String v2){
        if(operStr.equals("=")){
            return (v1.compareTo(v2) == 0)?true:false;
        }else if(operStr.equals("!=")){
            return (v1.compareTo(v2) == 0)?false:true;
        }else if(operStr.equals(">")){
            return (v1.compareTo(v2) > 0)? true:false;
        }else if(operStr.equals(">=")){
            return (v1.compareTo(v2) < 0)?false:true;
        }else if(operStr.equals("<")){
            return (v1.compareTo(v2) < 0)?true:false;
        }else if(operStr.equals("<=")){
            return (v1.compareTo(v2) > 0)?false:true;
        }
        return false;
    }
    /*compare Integer*/
    private static boolean compare(Integer v1,String operStr,Integer v2){
        if(operStr.equals("=")){
            return (v1.compareTo(v2) == 0)?true:false;
        }else if(operStr.equals("!=")){
            return (v1.compareTo(v2) == 0)?false:true;
        }else if(operStr.equals(">")){
            return (v1.compareTo(v2) > 0)? true:false;
        }else if(operStr.equals(">=")){
            return (v1.compareTo(v2) < 0)?false:true;
        }else if(operStr.equals("<")){
            return (v1.compareTo(v2) < 0)?true:false;
        }else if(operStr.equals("<=")){
            return (v1.compareTo(v2) > 0)?false:true;
        }
        return false;
    }
    /*compare Double*/
    private static boolean compare(Double v1,String operStr,Double v2){
        if(operStr.equals("=")){
            return (v1.compareTo(v2) == 0)?true:false;
        }else if(operStr.equals("!=")){
            return (v1.compareTo(v2) == 0)?false:true;
        }else if(operStr.equals(">")){
            return (v1.compareTo(v2) > 0)? true:false;
        }else if(operStr.equals(">=")){
            return (v1.compareTo(v2) < 0)?false:true;
        }else if(operStr.equals("<")){
            return (v1.compareTo(v2) < 0)?true:false;
        }else if(operStr.equals("<=")){
            return (v1.compareTo(v2) > 0)?false:true;
        }
        return false;
    }
    /*compare Float*/
    private static boolean compare(Float v1,String operStr,Float v2){
        if(operStr.equals("=")){
            return (v1.compareTo(v2) == 0)?true:false;
        }else if(operStr.equals("!=")){
            return (v1.compareTo(v2) == 0)?false:true;
        }else if(operStr.equals(">")){
            return (v1.compareTo(v2) > 0)? true:false;
        }else if(operStr.equals(">=")){
            return (v1.compareTo(v2) < 0)?false:true;
        }else if(operStr.equals("<")){
            return (v1.compareTo(v2) < 0)?true:false;
        }else if(operStr.equals("<=")){
            return (v1.compareTo(v2) > 0)?false:true;
        }
        return false;
    }
    /*compare Long*/
    private static boolean compare(Long v1,String operStr,Long v2){
        if(operStr.equals("=")){
            return (v1.compareTo(v2) == 0)?true:false;
        }else if(operStr.equals("!=")){
            return (v1.compareTo(v2) == 0)?false:true;
        }else if(operStr.equals(">")){
            return (v1.compareTo(v2) > 0)? true:false;
        }else if(operStr.equals(">=")){
            return (v1.compareTo(v2) < 0)?false:true;
        }else if(operStr.equals("<")){
            return (v1.compareTo(v2) < 0)?true:false;
        }else if(operStr.equals("<=")){
            return (v1.compareTo(v2) > 0)?false:true;
        }
        return false;
    }
    /*compare BigDecimal*/
    private static boolean compare(BigDecimal v1,String operStr,BigDecimal v2){
        if(operStr.equals("=")){
            return (v1.compareTo(v2) == 0)?true:false;
        }else if(operStr.equals("!=")){
            return (v1.compareTo(v2) == 0)?false:true;
        }else if(operStr.equals(">")){
            return (v1.compareTo(v2) > 0)? true:false;
        }else if(operStr.equals(">=")){
            return (v1.compareTo(v2) < 0)?false:true;
        }else if(operStr.equals("<")){
            return (v1.compareTo(v2) < 0)?true:false;
        }else if(operStr.equals("<=")){
            return (v1.compareTo(v2) > 0)?false:true;
        }
        return false;
    }
    /*compare Boolean*/
    private static boolean compare(Boolean v1,String operStr,Boolean v2){
        if(operStr.equals("=")){
            return (v1.compareTo(v2) == 0)?true:false;
        }else if(operStr.equals("!=")){
            return (v1.compareTo(v2) == 0)?false:true;
        }else if(operStr.equals(">")){
            return (v1.compareTo(v2) > 0)? true:false;
        }else if(operStr.equals(">=")){
            return (v1.compareTo(v2) < 0)?false:true;
        }else if(operStr.equals("<")){
            return (v1.compareTo(v2) < 0)?true:false;
        }else if(operStr.equals("<=")){
            return (v1.compareTo(v2) > 0)?false:true;
        }
        return false;
    }

    public static Field getFieldByName(String fieldName, Class<?> clazz) {
        Field[] selfFields = clazz.getDeclaredFields();

        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
        return null;
    }

   public static String[] checkListDuplicate(List checkList, String[] fields) {

        List keyList;
        Object value;

        Set<String> result = new HashSet<>();

        for (String field : fields) {
            keyList = new ArrayList<>();
            for (Object member : checkList) {
                value = getFieldValueByName(field, member);
                if (value != null) {
                    if (keyList.contains(value)){
                        result.add(field);
                        break;
                    } else {
                        keyList.add(value);
                    }
                }
            }
        }

        return result.toArray(new String[]{});
    }

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

    public static String getFixedSplit(String splitName,int number) {

        String splitData = "";
        if (StringUtils.isNotBlankText(splitName)) {

        String[] splitDataArray = splitName.split(CommonConstants.CHARACTER_VERTICAL_BAR);
            if (splitDataArray.length <= number-1) {
                return splitData;
            }else {
                splitData = splitDataArray[number-1];
            }
        }
            return splitData;
        }
}

