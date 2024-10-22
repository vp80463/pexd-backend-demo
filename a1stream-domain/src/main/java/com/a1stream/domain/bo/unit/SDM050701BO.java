package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050701BO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String promotionCode;

    private String promotionName;

    private String dealerCode;

    private String dealerName;
    
    private String modelCode;

    private String modelName;

    private String frameNo;

    private Long promotionListId;

    private Long promotionOrderId;

    private String canEnjoyFlag;

}
