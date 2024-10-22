package com.a1stream.common.facade;

import org.springframework.stereotype.Component;

import com.a1stream.common.service.FileImportService;
import com.a1stream.domain.vo.CmmFileLoadSettingVO;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Component
public class FileImportFacade {

    @Resource
    private FileImportService fileImportService;

    public CmmFileLoadSettingVO getFileLoadSettingVO(String languageId, String programId, String templateName) {

        return fileImportService.getFileLoadSettingListByProgramId(languageId, programId, templateName);
    }

    public CmmFileLoadSettingVO getFileLoadSettingForTxtVO(String languageId, String programId, String importFileType) {

        return fileImportService.getFileLoadSettingForTxtVO(languageId, programId, importFileType);
    }
}
