package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;





public class DemandForecastBatchBO{

    /**
	 *
	 */
	private static final long serialVersionUID = 8066701070979431762L;
	String[] columnValues = new String[20];

    /**
     * @param data
     * @param siteId
     * @param facilityId
     * @param productId
     */
    public DemandForecastBatchBO(Object[] data, String siteId, String facilityId,String productId) {

       if(data ==null || data.length != 16) {
         throw new IllegalAccessError("The length of input array must be 16!");
       }

//       System.arraycopy(data, 0, columnValues, 0, data.length);
       for (int colIdx = 0; colIdx < data.length; colIdx++) {
         Object item = data[colIdx];
         this.columnValues[colIdx] = getFieldValue(colIdx, item);
       }

       //
       this.setSiteid(siteId);
       this.setfacilityid(facilityId);
       this.setProductid(productId);
    }

    /**
     * @return
     */
    private String getFieldValue(int colIdx, Object item) {

      if(colIdx < 14) { //Bigdecimal

        return (item ==null) ? "0" :  ((BigDecimal)item).toString();
      }else { //String.
        return (String)item;
      }
    }

    /**
     * @return the j1total
     */
    public String getJ1total() {
        return columnValues[0];
    }

    /**
     * @param j1total the j1total to set
     */
    public void setJ1total(String j1total) {
        columnValues[0] = j1total;
    }
    /**
     * @return the j2total
     */
    public String getJ2total() {
        return columnValues[1];
    }

    /**
     * @param j2total the j2total to set
     */
    public void setJ2total(String j2total) {
        columnValues[1] = j2total;
    }
    /**
     * @return the month01
     */
    public String getMonth01() {
        return columnValues[2];
    }

    /**
     * @param month01 the month01 to set
     */
    public void setMonth01(String month01) {
        columnValues[2] = month01;
    }
    /**
     * @return the month02
     */
    public String getMonth02() {
        return columnValues[3];
    }

    /**
     * @param month02 the month02 to set
     */
    public void setMonth02(String month02) {
        columnValues[3] = month02;
    }
    /**
     * @return the month03
     */
    public String getMonth03() {
        return columnValues[4];
    }

    /**
     * @param month03 the month03 to set
     */
    public void setMonth03(String month03) {
        columnValues[4] = month03;
    }
    /**
     * @return the month04
     */
    public String getMonth04() {
        return columnValues[5];
    }

    /**
     * @param month04 the month04 to set
     */
    public void setMonth04(String month04) {
        columnValues[5] = month04;
    }
    /**
     * @return the month05
     */
    public String getMonth05() {
        return columnValues[6];
    }

    /**
     * @param month05 the month05 to set
     */
    public void setMonth05(String month05) {
        columnValues[6] = month05;
    }
    /**
     * @return the month06
     */
    public String getMonth06() {
        return columnValues[7];
    }

    /**
     * @param month06 the month06 to set
     */
    public void setMonth06(String month06) {
        columnValues[7] = month06;
    }
    /**
     * @return the month07
     */
    public String getMonth07() {
        return columnValues[8];
    }

    /**
     * @param month07 the month07 to set
     */
    public void setMonth07(String month07) {
        columnValues[8] = month07;
    }
    /**
     * @return the month08
     */
    public String getMonth08() {
        return columnValues[9];
    }

    /**
     * @param month08 the month08 to set
     */
    public void setMonth08(String month08) {
        columnValues[9] = month08;
    }
    /**
     * @return the month09
     */
    public String getMonth09() {
        return columnValues[10];
    }

    /**
     * @param month09 the month09 to set
     */
    public void setMonth09(String month09) {
        columnValues[10] = month09;
    }
    /**
     * @return the month10
     */
    public String getMonth10() {
        return columnValues[11];
    }

    /**
     * @param month10 the month10 to set
     */
    public void setMonth10(String month10) {
        columnValues[11] = month10;
    }
    /**
     * @return the month11
     */
    public String getMonth11() {
        return columnValues[12];
    }

    /**
     * @param month11 the month11 to set
     */
    public void setMonth11(String month11) {
        columnValues[12] = month11;
    }
    /**
     * @return the month12
     */
    public String getMonth12() {
        return columnValues[13];
    }

    /**
     * @param month12 the month12 to set
     */
    public void setMonth12(String month12) {
        columnValues[13] = month12;
    }
    /**
     * @return the registerdate
     */
    public String getRegisterdate() {
        return columnValues[14];
    }

    /**
     * @param registerdate the registerdate to set
     */
    public void setRegisterdate(String registerdate) {
        columnValues[14] = registerdate;
    }
    /**
     * @return the firstorderdate
     */
    public String getFirstorderdate() {
        return columnValues[15];
    }

    /**
     * @param firstorderdate the firstorderdate to set
     */
    public void setFirstorderdate(String firstorderdate) {
        columnValues[15] = firstorderdate;
    }
    /**
     * @return the siteid
     */
    public String getSiteid() {
        return columnValues[16];
    }

    /**
     * @param siteid the siteid to set
     */
    public void setSiteid(String siteid) {
        columnValues[16] = siteid;
    }

    /**
     * @return the facilityid
     */
    public String getFacilityid() {
        return columnValues[17];
    }

    /**
     * @param facilityid the facilityid to set
     */
    public void setfacilityid(String facilityid) {
        columnValues[17] = facilityid;
    }
    /**
     * @return the productid
     */
    public String getProductid() {
        return columnValues[18];
    }

    /**
     * @param productid the productid to set
     */
    public void setProductid(String productid) {
        columnValues[18] = productid;
    }
    /**
     * @return the productcategoryid
     */
    public String getProductcategoryid() {
        return columnValues[19];
    }

    /**
     * @param productcategoryid the productcategoryid to set
     */
    public void setProductcategoryid(String productcategoryid) {
        columnValues[19] = productcategoryid;
    }




}