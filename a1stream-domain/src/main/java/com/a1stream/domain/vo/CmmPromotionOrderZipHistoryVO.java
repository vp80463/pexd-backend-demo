package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPromotionOrderZipHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long promotionOrderZipHistoryId;

    private Long promotionListId;

    private Long promotionOrderId;

    private String zipPath;

    private String zipNm;

    private String backupPath;


}
