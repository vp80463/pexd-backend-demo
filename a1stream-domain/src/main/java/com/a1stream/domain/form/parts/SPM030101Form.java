package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM030101BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long supplierId;

    private String supplierCd;

    private String supplierNm;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String shipmentNo;

    private String invoiceNo;

    private String caseNo;

    private Long receiptManifestId;

    private List<SPM030101BO> allTableDataList;

    private List<Long> receiptSlipIds;
}
