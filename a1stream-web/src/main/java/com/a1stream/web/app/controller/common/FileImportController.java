package com.a1stream.web.app.controller.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.facade.FileImportFacade;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.model.FileLoadForm;
import com.a1stream.domain.vo.CmmFileLoadSettingVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("common/fileImport")
public class FileImportController implements RestProcessAware {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    private ExcelFileExporter exporter;

    @Autowired
    public void setExporter(ExcelFileExporter exporter) {
        this.exporter = exporter;
    }

    @Resource
    private FileImportFacade fileImportFacade;

    @PostMapping(value = "/templateDownload.json")
    public DownloadResponseView downloadExcelFile(@RequestBody final FileLoadForm form) {

        try {
            DownloadResponse templateExcel = exporter.generate(form.getTemplateName(), null, form.getTemplateName());
            return new DownloadResponseView(templateExcel);
        } catch (Exception e){
            throw new PJCustomException(BaseResult.GET_FILE_ERROR_MESSAGE);
        }
    }

    /**
     * Initializes the file import process by setting up necessary configurations based on the provided form.
     * This method prepares the file load settings, including template and title JSON configurations, API URLs for file operations,
     * and other relevant settings. It also handles language preferences for the template, defaulting to English if not specified.
     *
     * @param form The {@link FileLoadForm} containing initial data for file import, such as program ID and template name.
     * @return The updated {@link FileLoadForm} with configurations for file import, including API URLs, template details, and title JSON.
     */
    @PostMapping(value = "/importFileInitial.json")
    public FileLoadForm importFileInitial(@RequestBody final FileLoadForm form) {

        //Reserved language template switch, currently unknown language source, defaults to English
        String languageId = CommonConstants.LANGUAGE_EN;

        CmmFileLoadSettingVO result;
        if ("txt".equals(form.getImportFileType())){
            result = fileImportFacade.getFileLoadSettingForTxtVO(languageId, form.getProgramId(), form.getImportFileType());
        } else {
            result = fileImportFacade.getFileLoadSettingVO(languageId, form.getProgramId(), StringUtils.toNullIfBlank(form.getTemplateName()));
        }

        if (result != null){
            JSONObject templateJson = JSON.parseObject(result.getTemplateJson());
            JSONObject titleJson = JSON.parseObject(result.getTitleJson());

            List<Map.Entry<String, Object>> list = getEntries(templateJson);

            List<JSONObject> titleJsonList = new ArrayList<>();

            JSONObject errTitleJson = new JSONObject();
            errTitleJson.put(KEY, "error");
            errTitleJson.put(VALUE, CodedMessageUtils.getMessage("label.errorMessage"));
            titleJsonList.add(errTitleJson);

            for (Map.Entry<String, Object> member : list) {
                JSONObject newTitleJson = new JSONObject();
                newTitleJson.put(KEY, member.getKey());
                newTitleJson.put(VALUE, titleJson.get(member.getKey()));
                titleJsonList.add(newTitleJson);
            }

            JSONObject warTitleJson = new JSONObject();
            warTitleJson.put(KEY, "warning");
            warTitleJson.put(VALUE, CodedMessageUtils.getMessage("label.warningMessage"));
            titleJsonList.add(warTitleJson);

            form.setFileLoadApiUrl(result.getFileLoadApiUrl());
            form.setFileSaveApiUrl(result.getFileSaveApiUrl());
            form.setTemplateName(result.getFilePath());
            form.setTemplateJson(templateJson);
            form.setTitleJsonList(titleJsonList);
            form.setRemarks(result.getRemarks());
            form.setFileSize(result.getFileSize());
            form.setFileRows(result.getFileRows());
            form.setRedownloadFlag(result.getRedownloadFlag());
            form.setStartRow(result.getStartRow());
            form.setFileRedownloadApiUrl(result.getFileRedownloadApiUrl());
            form.setStartSheet(result.getStartSheet());
            form.setBackgroundSaveFlag(result.getBackgroundSaveFlag());
            form.setImportFileType(result.getImportFileType());
        }
        return form;
    }

    private static List<Map.Entry<String, Object>> getEntries(JSONObject templateJson) {
        List<Map.Entry<String, Object>> list = new ArrayList<>(templateJson.entrySet());
        list.sort((o1, o2) -> {
            Integer value1 = Integer.parseInt(StringUtils.toString(o1.getValue()));
            Integer value2 = Integer.parseInt(StringUtils.toString(o2.getValue()));
            return value1.compareTo(value2);
        });
        return list;
    }
}