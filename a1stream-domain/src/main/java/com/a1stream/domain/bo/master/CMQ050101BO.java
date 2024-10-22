package com.a1stream.domain.bo.master;

import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ050101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private CMQ050101InformationBO information = new CMQ050101InformationBO();

    private List<CMQ050101BasicInfoBO> basicInfoList = new ArrayList<>();
    private List<CMQ050101DemandDetailBO> demandDetailList = new ArrayList<>();
    private List<CMQ050101PurchaseControlBO> purchaseControlList = new ArrayList<>();
    private List<CMQ050101StockInfoBO> stockInfoList = new ArrayList<>();
}