package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050201BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String promoCd;
    private String promoNm;
    private Long promoId;
    private String modelCd;
    private String modelNm;
    private Long modelId;
    private String deleteFlag;
    //行状态，为true表示删除，为false表示新增
    private String status;

    private String errorMessage;
    private String warningMessage;
    transient List<String> error;
    transient List<Object[]> errorParam;
    transient List<String> warning;
    transient List<Object[]> warningParam;

}
