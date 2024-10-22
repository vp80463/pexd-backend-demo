package com.a1stream.common.bo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class FilesUploadUtilBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3555126833928917333L;

    /**
     * 据点ID
     */
    private String siteId;

    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务规则名称
     */
    private String businessRulesName;

    /**
     * 单文件
     */
    private transient MultipartFile singleFile;

    /**
     * 文件列表
     */
    private transient MultipartFile[] multipleFiles;

    private Map<String, String> oldAndNewFileNameMap;
}