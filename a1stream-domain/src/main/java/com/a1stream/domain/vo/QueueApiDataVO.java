package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class QueueApiDataVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long dbId;

    private String apiCd;

    private String apiDate;

    private String processKey;

    private String messageLotId;

    private String serverNm;

    private String actionType;

    private Integer sendTimes = CommonConstants.INTEGER_ZERO;

    private String status;

    private String statusMessage;

    private String tableNm;

    private Long tablePk;

    private String relationTableNm;

    private String relationTablePk;

    public static QueueApiDataVO create() {
        QueueApiDataVO entity = new QueueApiDataVO();
        entity.setDbId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
