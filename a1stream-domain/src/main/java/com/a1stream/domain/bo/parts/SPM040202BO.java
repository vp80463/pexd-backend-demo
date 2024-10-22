package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040202BO extends BaseBO{
    private static final long serialVersionUID = 1L;

    private String partsCd;
    private BigDecimal rop;
    private BigDecimal roq;
    private String sign;
    private String errorMessage;
    private String warningMessage;

    transient List<String> error;

    transient List<Object[]> errorParam;

    transient List<String> warning;

    transient List<Object[]> warningParam;
}
