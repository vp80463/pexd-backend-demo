package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvWarrantyBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String frameNo;
    private String warrantyEffectiveDate;
    private String warrantyexpiredDate;
    private String warrantyMileage;
    private String warrantyPolicyType;
    private String categoryType;
    private String rowStatus;

    private List<SvWarrantyItemBO> resultList = new ArrayList<>();
}