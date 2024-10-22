package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;


/**
 * @author dong zhen
 */
@Setter
@Getter
public class CmmMenuClickSituationVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 3074600684848025250L;

    private Long menuClickSituationId;

    private String customStatus;

    private Integer priority;

    private String menuCd;

    private String userId;

    private Integer clickCount;
}
