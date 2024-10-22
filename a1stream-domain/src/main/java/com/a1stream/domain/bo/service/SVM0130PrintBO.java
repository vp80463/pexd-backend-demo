package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0130PrintBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String orderStatusId;

    private String receptionPic;

    private String modelName;

    private String frameNo;

    private String phoneNumber;

    private String mechanicName;

    private String shopName;

    private String address;

    private String telephone;

    private String date;

    private String logo;

    private String brand;

    private String cashier;

    private String customerComment;

    private BigDecimal jobAmount;

    private BigDecimal partAmount;

    private BigDecimal depositAmt;

    private String serviceDate;

    private List<SVM0130PrintServiceJobBO> serviceJobList;

    private List<SVM0130PrintServicePartBO> servicePartList;

}
