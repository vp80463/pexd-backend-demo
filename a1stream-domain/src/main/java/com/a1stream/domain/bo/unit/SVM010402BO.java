package com.a1stream.domain.bo.unit;

import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010402BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    //basicInfo
    private String pointName;
    private String evFlag;
    private String modelCode;
    private String modelName;
    private String color;
    private String plateNo;
    private String frameNo;
    private String engineNo;
    private String batteryId1;
    private String batteryId2;
    private String soldDate;
    private String manufacturingDate;
    private String brandName;
    private String motorcycleCategory;
    private String qualityStatus;
    private String stockStatus;
    private String salesStatus;
    private String qualityStatusNm;
    private String stockStatusNm;
    private String salesStatusNm;
    private Long serializedProductId;

    //ConsumerInfo
    private List<SVM010402ConsumerInfoBO> consumerInfoList = new ArrayList<>();

    //TransactionHistory
    private List<SVM010402TransactionHistoryBO> transactionHistoryList = new ArrayList<>();

    //Service History
    private List<SVM010402ServiceHistoryBO> serviceHistoryList = new ArrayList<>();

    //consumerBasicInfo
    private String currentOwner;
    private SVM010402ConsumerInfoBO consumerBasicInfo;
}
