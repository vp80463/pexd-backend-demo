package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;


/**
 * @author dong zhen
 */
@Getter
@Setter
public class CmmFileLoadSettingVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 7151983142972274147L;

    private Long fileLoadId;

    private String typeId;

    private String languageId;

    private String programId;

    private String filePath;

    private String fileLoadApiUrl;

    private String fileValidApiUrl;

    private String fileSaveApiUrl;

    private String fileRedownloadApiUrl;

    private String templateJson;

    private String titleJson;

    private String remarks;

    private Integer fileSize = CommonConstants.INTEGER_ZERO;

    private Integer fileRows = CommonConstants.INTEGER_ZERO;

    private Integer startRow = CommonConstants.INTEGER_ZERO;

    private Integer startSheet = CommonConstants.INTEGER_ZERO;

    private String redownloadFlag;

    private String backgroundSaveFlag;

    private String importFileType;
}