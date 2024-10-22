package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;


/**
 * @author dong zhen
 */
@Getter
@Setter
public class CmmUploadFileVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -3943151782214144549L;

    private Long fileId;

    private String businessType;

    private Long businessId;

    private String fileName;

    private String oriFileName;

    private String uploadPath;

    private String uploadDate;

    private String confusionName;
}