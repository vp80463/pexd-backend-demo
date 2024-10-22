package com.a1stream.common.service;

import com.a1stream.common.bo.FilesUploadReturnBO;
import com.a1stream.common.manager.FileUploadManager;
import com.a1stream.common.manager.RunShellManager;
import com.a1stream.common.model.FileUploadForm;
import com.a1stream.domain.entity.CmmPromotionOrderZipHistory;
import com.a1stream.domain.entity.CmmUploadFile;
import com.a1stream.domain.repository.CmmPromotionOrderZipHistoryRepository;
import com.a1stream.domain.repository.CmmUploadFileRepository;
import com.a1stream.domain.repository.UploadAccCatalogRepository;
import com.a1stream.domain.vo.CmmPromotionOrderZipHistoryVO;
import com.a1stream.domain.vo.CmmUploadFileVO;
import com.a1stream.domain.vo.UploadAccCatalogVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dong zhen
 */
@Service
public class FileUploadService {

    @Resource
    private CmmUploadFileRepository cmmUploadFileRepository;

    @Resource
    private FileUploadManager fileUploadManager;

    @Resource
    private RunShellManager runShellManager;

    @Resource
    private CmmPromotionOrderZipHistoryRepository cmmPromotionOrderZipHistoryRepository;

    @Resource UploadAccCatalogRepository uploadAccCatalogRepository;

    public UploadAccCatalogVO getUploadAccCatalog(String id) {
        return BeanMapUtils.mapTo(uploadAccCatalogRepository.findByParameterTypeId(id),UploadAccCatalogVO.class);
    }

    public String getFileUrl(String confusionName, String type) {

        CmmUploadFile cmmUploadFile = cmmUploadFileRepository.findByBusinessTypeAndConfusionName(type, confusionName);
        String url = "";
        if (cmmUploadFile != null){
            url = cmmUploadFile.getUploadPath() + "/" + cmmUploadFile.getFileName();
        }
        return url;
    }

    public void deleteFileUrl(String confusionName, String businessType) {

        CmmUploadFile cmmUploadFile = cmmUploadFileRepository.findByBusinessTypeAndConfusionName(businessType, confusionName);
        cmmUploadFileRepository.delete(cmmUploadFile);
    }

    public CmmUploadFileVO getUploadFileVO(String confusionName, String businessType) {

        CmmUploadFile cmmUploadFile = cmmUploadFileRepository.findByBusinessTypeAndConfusionName(businessType, confusionName);
        return BeanMapUtils.mapTo(cmmUploadFile, CmmUploadFileVO.class);
    }

    public List<FilesUploadReturnBO> getImageUrlList(FileUploadForm form) {

        return fileUploadManager.getUrlListByBusinessType(form.getBusinessType());
    }

    public CmmPromotionOrderZipHistoryVO getCmmPromotionOrderZipHistoryVO(Long promotionOrderZipHistoryId) {

        CmmPromotionOrderZipHistory cmmPromotionOrderZipHistory = cmmPromotionOrderZipHistoryRepository.findByPromotionOrderZipHistoryId(promotionOrderZipHistoryId);
        return BeanMapUtils.mapTo(cmmPromotionOrderZipHistory, CmmPromotionOrderZipHistoryVO.class);
    }

    public byte[] downloadFileFromSftp(String fileUrl) {

        return runShellManager.downloadFileFromSftp(fileUrl);
    }
}
