package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class RemindSettingVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long remindSettingId;

    private String remindType;

    private String remindSubject;

    private String stepContent;

    private String baseDateType;

    private BigDecimal days = BigDecimal.ZERO;

    private BigDecimal continueDays = BigDecimal.ZERO;

    private String roleList;
}
