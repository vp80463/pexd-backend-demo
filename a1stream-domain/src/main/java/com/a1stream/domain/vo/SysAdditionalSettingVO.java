package com.a1stream.domain.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


/**
 * @author Liu Chaoran
 */
@Setter
@Getter
public class SysAdditionalSettingVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8816169058375246481L;

    private Long addSettingId;

    private String menuId;

    private String menuCode;

    private String menuName;

    private String workDayCheck;

    private String accountMonthCheck;

    private String partsStocktakingCheck;

    private String unitStocktakingCheck;
}
