package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0120PrintBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String modelName;
    private String noPlate;
    private String consumerName;
    private String receptionPic;
    private String brand;
    private BigDecimal mileage;
    private String frameNo;
    private String mechanicName;
    private String vipNo;
    private String shopName;
    private String address;
    private String telephone;
    private String date;
    private String logo;
    private String cashier;
    private String orderDate;
    private String customerComment;
    private String userName;
    private String serviceDate;
    private BigDecimal jobAmount;
    private BigDecimal partAmount;

    private List<SVM0120PrintServiceJobBO> serviceJobList;
    private List<SVM0120PrintServicePartBO> servicePartList;

}
