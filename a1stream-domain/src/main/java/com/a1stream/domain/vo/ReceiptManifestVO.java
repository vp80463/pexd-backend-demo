package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReceiptManifestVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long receiptManifestId;

    private String importDate;

    private String importTime;

    private String supplierInvoiceNo;

    private String supplierShipmentNo;

    private String supplierShippedDate;

    private Long fromOrganization;

    private Long toOrganization;

    private Long fromFacilityId;

    private Long toFacilityId;

    private String manifestStatus;

    private String productClassification;

    public static ReceiptManifestVO create() {
        ReceiptManifestVO entity = new ReceiptManifestVO();
        entity.setReceiptManifestId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
