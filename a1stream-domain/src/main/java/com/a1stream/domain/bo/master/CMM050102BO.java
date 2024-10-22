package com.a1stream.domain.bo.master;

import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050102BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private CMM050102BasicInfoBO basicInfo = new CMM050102BasicInfoBO();
    private CMM050102SalesControlBO salesControl = new CMM050102SalesControlBO();

    private List<CMM050102PurchaseControlBO> purchaseControlList = new ArrayList<>();

}