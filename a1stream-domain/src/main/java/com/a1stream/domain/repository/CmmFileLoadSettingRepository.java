package com.a1stream.domain.repository;

import com.a1stream.domain.entity.CmmFileLoadSetting;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;


/**
 * @author mid2010
 */
@Repository
public interface CmmFileLoadSettingRepository extends JpaExtensionRepository<CmmFileLoadSetting, Long> {

    CmmFileLoadSetting findByTypeIdAndLanguageIdAndProgramIdAndFilePath(String anImport, String languageId, String programId, String templateName);

    CmmFileLoadSetting findByTypeIdAndLanguageIdAndProgramIdAndImportFileType(String anImport, String languageId, String programId, String importFileType);
}
