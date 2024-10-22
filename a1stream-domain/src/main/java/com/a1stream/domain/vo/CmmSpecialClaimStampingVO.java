package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSpecialClaimStampingVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long specialClaimStampingId;

    private Long specialClaimId;

    private String stampingStyle;

    private String serialNoFrom;

    private String serialNoTo;


}
