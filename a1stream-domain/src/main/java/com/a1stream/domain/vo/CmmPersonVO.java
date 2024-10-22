package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPersonVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long personId;

    private String personCd;

    private String personNm;

    private String firstNm;

    private String middleNm;

    private String lastNm;

    private String fromDate;

    private String toDate;

    private String personType;

    private String genderType;

    private String idNo;

    private String photoUrl;

    private String contactTel;

    private String contactMail;

    private Long provinceId;

    private String provinceNm;

    private Long cityId;

    private String cityNm;

    private String address1;

    private String address2;

    private String postCode;

    private String electronicSignatureUrl;

    private String extendList;

    private String userId;

    private String faxNo;

    private String personRetrieve;

    public static CmmPersonVO create() {
        CmmPersonVO entity = new CmmPersonVO();
        entity.setPersonId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
