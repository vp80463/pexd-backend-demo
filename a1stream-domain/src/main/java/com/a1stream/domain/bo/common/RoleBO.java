package com.a1stream.domain.bo.common;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roleId;

    private String roleCode;

    private String roleName;

    private Date effectiveDate;

    private Date expiredDate;

}