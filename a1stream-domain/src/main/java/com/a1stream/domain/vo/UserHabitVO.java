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
public class UserHabitVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -2130751636836604547L;

    private Long userHabitId;

    private String userId;

    private String userHabitTypeId;

    private String seqNo;

    private String habitContent;
}
