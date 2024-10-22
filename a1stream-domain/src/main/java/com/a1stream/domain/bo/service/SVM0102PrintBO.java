package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0102PrintBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String receptionPic;
    private String consumerName;
    private String brand;
    private String modelName;
    private BigDecimal mileage;
    private String noPlate;
    private String frameNo;
    private String phoneNumber;
    private String soldDate;
    private String mechanicName;
    private String shopName;
    private String address;
    private String telephone;
    private String welcomeStaff;
    private String orderStatusId;
    private String cashier;
    private String customerComment;
    private String employeeCode;
    private String relationShipId;
    private String relationShip;
    private String ticketNo;
    private String serviceDate;
    private BigDecimal depositAmt;

    private BigDecimal jobAmount;
    private BigDecimal partAmount;


    private List<SVM0102PrintServiceHistoryBO> serviceHistoryList;

    private List<SVM0102PrintServiceJobBO> serviceJobList;

    private List<SVM0102PrintServicePartBO> servicePartList;

    private String date;

    private String logo;

}
