package com.a1stream.domain.bo.parts;

import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM021401BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String requestStatus;

    private String requestStatusId;

    private Long returnRequestListId;

    private String recommendDate;

    private String requestDate;

    private String approvedDate;

    private String expireDate;

    private Long pointId;

    private String point;

    private List<SPM021402BO> tableDataList;
}
