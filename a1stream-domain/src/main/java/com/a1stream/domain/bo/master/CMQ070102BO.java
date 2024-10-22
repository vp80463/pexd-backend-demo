package com.a1stream.domain.bo.master;

import java.io.Serializable;
import java.util.List;

import com.a1stream.domain.bo.common.RoleBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ070102BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long employeeId;
    private String employee;
    private String userCd;
    private String email;
    private String dateFrom;
    private String dateTo;
    private String status;
    private String userId;

    private List<RoleBO> roleList; // 自定义List接收传入参数

//    private BaseTableData<RoleBO> tableData; // 接收有变更的Table记录（insertRecords | removeRecords | updateRecords）
}
