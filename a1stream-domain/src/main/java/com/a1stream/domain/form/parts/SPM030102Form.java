package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.parts.SPM030102BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030102Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String caseNo;

    private Long pointId;

    private String point;

    private String supplier;

    private BaseTableData<SPM030102BO> tableDataList;

    private Long receiptManifestItemId;

    private List<SPM030102BO> allTableDataList;

    private String orderNo;

    private Long orderPartsId;

    private Integer updateCount;
}
