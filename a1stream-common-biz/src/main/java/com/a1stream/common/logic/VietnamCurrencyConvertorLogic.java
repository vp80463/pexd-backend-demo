package com.a1stream.common.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VietnamCurrencyConvertorLogic {

    private static final VietnamCurrencyConvertorLogic m_instance = new VietnamCurrencyConvertorLogic();

    public static Map<Integer, String> unit;
    public static List<Integer> numberList ;
    public static boolean needHundredJoinChar; 
    public static String hundredJoinChar;
    public static String zeroString;
    
    public static VietnamCurrencyConvertorLogic getInstance(){
        return m_instance;
    }

    private VietnamCurrencyConvertorLogic(){
        this.unit = new HashMap<Integer, String>();
        this.numberList = new ArrayList<Integer>();
        this.needHundredJoinChar = true;
        this.hundredJoinChar = "linh";
        this.zeroString = "không";
  
        loadVietnamConfig();
    }

    public static String convert(BigDecimal number) {

        if(number.intValue()==0){
        	return firstCharUpperCase(zeroString + " đồng");
        }
        
        return firstCharUpperCase(exchage(number) + " đồng");
    }

    public static String exchage(BigDecimal orgNumber) {
        
        if (orgNumber.intValue() == 0) return zeroString;
        String numStr = String.valueOf(orgNumber.longValue());
        StringBuilder sb = new StringBuilder();
        
        //process over than 1 billion
        if(numStr.length() > 9){
           String tyStr = numStr.substring(0, numStr.length()-9);
           sb.append(exchage(new BigDecimal(tyStr)));
           sb.append(" " + unit.get(1000000000));
        }
            
        String tStr = "         " + numStr;
        numStr = tStr.substring(tStr.length() - 9);

        //process million
        String millionStr = getUnderHundred(numStr.substring(0,3));
        if(!millionStr.equals("")){
            sb.append(" " + millionStr);
            sb.append(" " + unit.get(1000000));
        }
        
        //thousand
        String thousandStr = getUnderHundred(numStr.substring(3,6));
        if(!thousandStr.equals("")){
            sb.append(" " + thousandStr);
            sb.append(" " + unit.get(1000));
        }
        
        //hundred
        String hundredStr = getUnderHundred(numStr.substring(6,9));
        if(!hundredStr.equals("")){
            sb.append(" " + hundredStr);
        }
        
        return sb.toString().trim();
    }

    public static String getUnderHundred(String numStr) {
        if(numStr.trim().equals("")) return numStr.trim();
        if(numStr.trim().equals("000")) return "";

        if(numStr.length() > 3 )  numStr = numStr.substring(numStr.length()-3);
        //left add space
        if(numStr.length() == 2)  numStr = " " + numStr;
        if(numStr.length() == 1)  numStr = "  " + numStr;

        String hundredChar = numStr.substring(0,1);
        String tenChar = numStr.substring(1,2);
        String numChar = numStr.substring(2,3);

        StringBuilder sb = new StringBuilder();

        //process Hundred
        if(!hundredChar.equals(" ")){
            sb.append(unit.get(Integer.valueOf(hundredChar).intValue()));
            sb.append(" " + unit.get(100));
        }

        //process ten
        if(!tenChar.equals(" ")){
            if(!tenChar.equals("0")){
                //"mot muoi" not to need
                if(!tenChar.equals("1")){
                    sb.append(" " + unit.get(Integer.valueOf(tenChar).intValue()));
                    sb.append(" mươi");
                }else{
                    sb.append(" mười");
                }
            }else{
                if(!numChar.equals("0")){
                    sb.append(" " + hundredJoinChar);
                }
            }
        }

        //process num
        if(!numChar.equals("0")){
            if(numChar.equals("5")){//when is 5,it's have two case: "lam" &  "nam"
                if(tenChar.equals("0") || tenChar.equals(" ")){ //
                    sb.append(" năm");
                }else{
                    sb.append(" lăm");
                }
            }else if(numChar.equals("1")){
                if(tenChar.equals("0") || tenChar.equals(" ") || tenChar.equals("1")){ //
                    sb.append(" một");
                }else{
                    sb.append(" mốt");
                }
            }else{
                sb.append(" " + unit.get(Integer.valueOf(numChar).intValue()));
            }
        }

        return sb.toString().trim();
    }

    private static String firstCharUpperCase(String str) {

    	StringBuilder sb = new StringBuilder();

    	sb.append(str.substring(0, 1).toUpperCase());
    	sb.append(str.substring(1));

    	return sb.toString();
    }

    public static void loadVietnamConfig() {
        unit.put(1000000000, "tỷ");
        unit.put(1000000, "triệu");
        unit.put(1000, "nghìn");
        unit.put(100, "trăm");
        unit.put(90, "chín mươi");
        unit.put(80, "tám mươi");
        unit.put(70, "bảy mươi");
        unit.put(60, "sáu mươi");
        unit.put(50, "năm mươi");
        unit.put(40, "bốn mươi");
        unit.put(30, "ba mươi");
        unit.put(20, "hai mươi");
        unit.put(19, "mười chín");
        unit.put(18, "mười tám");
        unit.put(17, "mười băy");
        unit.put(16, "mười sáu");
        unit.put(15, "mười lăm");
        unit.put(14, "mười bốn");
        unit.put(13, "mười ba");
        unit.put(12, "mười hai");
        unit.put(11, "mười một");
        unit.put(10, "mười");
        unit.put(9, "chín");
        unit.put(8, "tám");
        unit.put(7, "bảy");
        unit.put(6, "sáu");
        unit.put(5, "năm");
        unit.put(4, "bốn");
        unit.put(3, "ba");
        unit.put(2, "hai");
        unit.put(1, "một");
        unit.put(0, "không");

        numberList.add(1000000000);
        numberList.add(1000000);
        numberList.add(1000);
        numberList.add(100);
        numberList.add(90);
        numberList.add(80);
        numberList.add(70);
        numberList.add(60);
        numberList.add(50);
        numberList.add(40);
        numberList.add(30);
        numberList.add(20);
        numberList.add(19);
        numberList.add(18);
        numberList.add(17);
        numberList.add(16);
        numberList.add(15);
        numberList.add(14);
        numberList.add(13);
        numberList.add(12);
        numberList.add(11);
        numberList.add(10);
        numberList.add(9);
        numberList.add(8);
        numberList.add(7);
        numberList.add(6);
        numberList.add(5);
        numberList.add(4);
        numberList.add(3);
        numberList.add(2);
        numberList.add(1);
    }

    public static void loadChineseConfig() {

        unit.put(100000000, "亿");
        unit.put(10000, "万");
        unit.put(1000, "仟");
        unit.put(100, "佰");
        unit.put(10, "拾");
        unit.put(9, "玖");
        unit.put(8, "捌");
        unit.put(7, "柒");
        unit.put(6, "陆");
        unit.put(5, "伍");
        unit.put(4, "肆");
        unit.put(3, "叁");
        unit.put(2, "贰");
        unit.put(1, "壹");

        numberList.add(100000000);
        numberList.add(10000);
        numberList.add(1000);
        numberList.add(100);
        numberList.add(10);
        numberList.add(9);
        numberList.add(8);
        numberList.add(7);
        numberList.add(6);
        numberList.add(5);
        numberList.add(4);
        numberList.add(3);
        numberList.add(2);
        numberList.add(1);
    }

    public static void loadUsaConfig() {
        unit.put(1000000000, "billion");
        unit.put(1000000, "million");
        unit.put(1000, "thousand");
        unit.put(100, "hundred");
        unit.put(90, "ninety");
        unit.put(80, "eighty");
        unit.put(70, "seventy");
        unit.put(60, "sixty");
        unit.put(50, "fifty");
        unit.put(40, "forty");
        unit.put(30, "thirty");
        unit.put(20, "twenty");
        unit.put(19, "nineteen");
        unit.put(18, "eighteen");
        unit.put(17, "seventeen");
        unit.put(16, "sixteen");
        unit.put(15, "fifteen");
        unit.put(14, "fourteen");
        unit.put(13, "thirteen");
        unit.put(12, "twelve");
        unit.put(11, "eleven");
        unit.put(10, "ten");
        unit.put(9, "nine");
        unit.put(8, "eight");
        unit.put(7, "seven");
        unit.put(6, "six");
        unit.put(5, "five");
        unit.put(4, "four");
        unit.put(3, "three");
        unit.put(2, "two");
        unit.put(1, "one");

        numberList.add(1000000000);
        numberList.add(1000000);
        numberList.add(1000);
        numberList.add(100);
        numberList.add(90);
        numberList.add(80);
        numberList.add(70);
        numberList.add(60);
        numberList.add(50);
        numberList.add(40);
        numberList.add(30);
        numberList.add(20);
        numberList.add(19);
        numberList.add(18);
        numberList.add(17);
        numberList.add(16);
        numberList.add(15);
        numberList.add(14);
        numberList.add(13);
        numberList.add(12);
        numberList.add(11);
        numberList.add(10);
        numberList.add(9);
        numberList.add(8);
        numberList.add(7);
        numberList.add(6);
        numberList.add(5);
        numberList.add(4);
        numberList.add(3);
        numberList.add(2);
        numberList.add(1);
    }
}
