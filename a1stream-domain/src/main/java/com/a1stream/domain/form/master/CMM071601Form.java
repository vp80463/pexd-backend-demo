package com.a1stream.domain.form.master;

import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.master.CMM071601BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM071601Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String dateFrom;

    private String dateTo;

    private String status;

    private List<String> type = new ArrayList<>();

    private List<CMM071601BO> gridData = new ArrayList<>();

}
