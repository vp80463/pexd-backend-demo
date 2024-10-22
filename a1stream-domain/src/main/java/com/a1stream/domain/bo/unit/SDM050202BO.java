package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050202BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dealerCd;
    private String dealerNm;
    private String dealerId;
    private String pointCd;
    private String pointNm;
    private Long pointId;
    private String modelCd;
    private String modelNm;
    private Long modelId;
    private String frameNo;
    private String stockMCFlag;
    private String promoCd;
    private String promoNm;
    private String fromDate;
    private String toDate;

    private String errorMessage;
    private String warningMessage;
    transient List<String> error;
    transient List<Object[]> errorParam;
    transient List<String> warning;
    transient List<Object[]> warningParam;

}
