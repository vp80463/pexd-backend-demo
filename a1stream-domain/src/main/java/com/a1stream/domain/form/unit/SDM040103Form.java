package com.a1stream.domain.form.unit;

/**
*
* 功能描述:
*
* @author mid2215
*/
import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SDM040103Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long registrationDocumentId;
    private String ownerTypeId;
    private String purchaseTypeId;
    private String userTypeId;
    private Long previousBikeBrandId;
    private Integer familyNum;
    private Integer bikeNum;
}
