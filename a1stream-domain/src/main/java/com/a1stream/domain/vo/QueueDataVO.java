package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.QueueStatus;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueueDataVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long qId;

    private String siteId;

    private String interfCd;

    private Integer sendTimes;

    private String status;

    private String relatedTable;

    private Long relatedId;

    private String body;

    public static QueueDataVO create(String siteId, String interfCd, String relatedTable, Long relatedId, String body) {

        QueueDataVO entity = new QueueDataVO();

        entity.setSiteId(siteId);
        entity.setSendTimes(CommonConstants.INTEGER_ZERO);
        entity.setRelatedTable(relatedTable);
        entity.setRelatedId(relatedId);
        entity.setInterfCd(interfCd);
        entity.setStatus(QueueStatus.WAITINGSEND.getCodeDbid());
        entity.setBody(body);
        entity.setUpdateProgram(CommonConstants.CHAR_BAR);

        return entity;
    }
}
