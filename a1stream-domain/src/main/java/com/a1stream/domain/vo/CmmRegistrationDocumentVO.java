package com.a1stream.domain.vo;

import java.io.Serial;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CmmRegistrationDocumentVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long registrationDocumentId;

    private String registrationDate;

    private String registrationTime;

    private Long facilityId;

    private Long consumerId;

    private Long salesOrderId;

    private Long serviceOrderId;

    private Long serializedProductId;

    private String useType;

    private String ownerType;

    private String purchaseType;

    private String psvBrandNm;

    private String pBikeNm;

    private String mtAtId;

    private Integer familyNum;

    private Integer numBike;

    private Long batteryId;

    public static CmmRegistrationDocumentVO create() {
        CmmRegistrationDocumentVO entity = new CmmRegistrationDocumentVO();
        entity.setRegistrationDocumentId(IdUtils.getSnowflakeIdWorker().nextId());
        return entity;
    }
}
