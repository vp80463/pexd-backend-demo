package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050202HeaderBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String promoCd;
    private String promoNm;
    private String fromDate;
    private String toDate;
}
