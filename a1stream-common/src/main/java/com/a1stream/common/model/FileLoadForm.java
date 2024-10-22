package com.a1stream.common.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class FileLoadForm {

    /**
     * 程序ID
     */
    private String programId;

    /**
     * 文件名
     */
    private String templateName;

    /**
     * 文件加载接口
     */
    private String fileLoadApiUrl;

    /**
     * 文件校验接口
     */
    private String fileValidApiUrl;

    /**
     * 文件保存接口
     */
    private String fileSaveApiUrl;

    /**
     * 文件重新下载接口
     */
    private String fileRedownloadApiUrl;

    /**
     * 模板JSON
     */
    private JSONObject templateJson;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 文件行数
     */
    private Integer fileRows;

    /**
     * 是否重新下载
     */
    private String redownloadFlag;

    /**
     * 起始行
     */
    private Integer startRow;

    /**
     * 起始页
     */
    private Integer startSheet;

    /**
     * 进入后台保存标识
     */
    private String backgroundSaveFlag;

    /**
     * 导入文件类型
     */
    private String importFileType;

    /**
     * 标题行
     */
    private List<JSONObject> titleJsonList = new ArrayList<>();

    /**
     * 数据行
     */
    private List<Map<String, Object>> data = new ArrayList<>();
}
