package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM050202BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050202Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long promotionListId;
    private Long promotionId;
    private String promotion;
    private String dateFrom;
    private String dateTo;

    private Object otherProperty;

    private List<SDM050202BO> importList;
    private List<SDM050202BO> gridDataList;

}
