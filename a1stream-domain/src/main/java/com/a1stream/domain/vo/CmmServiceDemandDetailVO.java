package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceDemandDetailVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceDemandDetailId;

    private Long productId;

    private Long serializedProductId;

    private Long serviceDemandId;

    private String fscResultFlag;

    private String fscUsage;

    private String fscResultDate;

    private String comment;


}
