package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050601BO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String promotionCode;

    private String promotionName;

    private String effectiveDate;

    private String expiredDate;

    private String uploadEndDate;

    private Long promotionListId;

}
