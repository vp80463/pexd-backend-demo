package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmAnnounceVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long announceId;

    private String siteId;

    private String notifyTypeId;

    private String title;

    private String coverUrl;

    private String fromDate;

    private String toDate;
}
