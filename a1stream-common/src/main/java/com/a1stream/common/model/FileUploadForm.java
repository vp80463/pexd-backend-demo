package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class FileUploadForm extends BaseForm{

    @Serial
    private static final long serialVersionUID = -4120066300086355868L;

    /**
     * 类型
     */
    private String businessType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 混淆名称
     */
    private String confusionName;

    /**
     * 文件路径
     */
    private String fileUrl;
}
