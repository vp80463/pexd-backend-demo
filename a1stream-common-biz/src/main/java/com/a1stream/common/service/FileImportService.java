package com.a1stream.common.service;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants.FileLoadMode;
import com.a1stream.domain.entity.CmmFileLoadSetting;
import com.a1stream.domain.repository.CmmFileLoadSettingRepository;
import com.a1stream.domain.vo.CmmFileLoadSettingVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Service
public class FileImportService {

    @Resource
    private CmmFileLoadSettingRepository cmmFileLoadSettingRepository;

    public CmmFileLoadSettingVO getFileLoadSettingListByProgramId(String languageId, String programId, String templateName) {

        CmmFileLoadSetting cmmFileLoadSetting = cmmFileLoadSettingRepository
                .findByTypeIdAndLanguageIdAndProgramIdAndFilePath(FileLoadMode.IMPORT, languageId, programId, templateName);
        return BeanMapUtils.mapTo(cmmFileLoadSetting, CmmFileLoadSettingVO.class);
    }

    public CmmFileLoadSettingVO getFileLoadSettingForTxtVO(String languageId, String programId, String importFileType) {

        CmmFileLoadSetting cmmFileLoadSetting = cmmFileLoadSettingRepository
                .findByTypeIdAndLanguageIdAndProgramIdAndImportFileType(FileLoadMode.IMPORT, languageId, programId, importFileType);
        return BeanMapUtils.mapTo(cmmFileLoadSetting, CmmFileLoadSettingVO.class);
    }
}
