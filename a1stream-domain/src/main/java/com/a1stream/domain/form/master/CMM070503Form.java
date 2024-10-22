package com.a1stream.domain.form.master;

import com.a1stream.domain.bo.master.CMM070503BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM070503Form extends CMM070503BO {

    private static final long serialVersionUID = 1L;

    private String userSearch;

    private String roleType;
}
