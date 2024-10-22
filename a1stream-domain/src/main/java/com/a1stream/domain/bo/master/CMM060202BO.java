package com.a1stream.domain.bo.master;

import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM060202BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long servicePackageId;

    private String packageCd;

    private String localDescription;

    private String salesDescription;

    private String englishDescription;

    private String serviceCategory;

    private String fromDate;

    private String toDate;

    private List<CMM060202Detail> categoryDetails;
    private List<CMM060202Detail> serviceDetails;
    private List<CMM060202Detail> partsDetails;
}