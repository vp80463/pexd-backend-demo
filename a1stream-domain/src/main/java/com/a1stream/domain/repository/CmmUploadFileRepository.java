package com.a1stream.domain.repository;

import com.a1stream.domain.entity.CmmUploadFile;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author mid2010
 */
@Repository
public interface CmmUploadFileRepository extends JpaExtensionRepository<CmmUploadFile, Long> {

    List<CmmUploadFile> findByBusinessType(String businessType);

    CmmUploadFile findByBusinessTypeAndFileName(String type, String filename);

    List<CmmUploadFile> findByBusinessId(Long businessId);

    CmmUploadFile findByBusinessTypeAndConfusionName(String type, String fileName);
}
