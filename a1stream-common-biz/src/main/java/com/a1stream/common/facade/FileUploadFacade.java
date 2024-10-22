
package com.a1stream.common.facade;

import com.a1stream.common.bo.FilesUploadReturnBO;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.model.FileUploadForm;
import com.a1stream.common.service.FileUploadService;
import com.a1stream.domain.vo.CmmPromotionOrderZipHistoryVO;
import com.a1stream.domain.vo.CmmUploadFileVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author dong zhen
 */
@Component
public class FileUploadFacade {

    @Resource
    private FileUploadService fileUploadService;

    public String getFileUrl(String confusionName, String businessType) {

        return fileUploadService.getFileUrl(confusionName, businessType);
    }

    public void deleteFileUrl(String confusionName, String businessType) {

        fileUploadService.deleteFileUrl(confusionName, businessType);
    }

    public FilesUploadReturnBO getFileUrlAndOriName(String confusionName, String businessType) {

        FilesUploadReturnBO filesUploadReturnBO = new FilesUploadReturnBO();
        CmmUploadFileVO cmmUploadFileVO = fileUploadService.getUploadFileVO(confusionName, businessType);
        String url = "";
        if (cmmUploadFileVO != null){
            url = cmmUploadFileVO.getUploadPath() + "/" + cmmUploadFileVO.getFileName();
        }
        filesUploadReturnBO.setName(Objects.requireNonNull(cmmUploadFileVO).getOriFileName());
        filesUploadReturnBO.setUrl(url);
        return filesUploadReturnBO;
    }

    public List<FilesUploadReturnBO> getImageUrlList(FileUploadForm form) {

        return fileUploadService.getImageUrlList(form);
    }

    public CmmPromotionOrderZipHistoryVO getCmmPromotionOrderZipHistoryVO(Long promotionOrderZipHistoryId) {

        return fileUploadService.getCmmPromotionOrderZipHistoryVO(promotionOrderZipHistoryId);
    }

    public byte[] downloadFileFromSftp(String fileUrl) {

        return fileUploadService.downloadFileFromSftp(fileUrl);
    }

    public String getPdfFile(){
        return fileUploadService.getUploadAccCatalog(PJConstants.UploadAccCatalogType.PDF_URL).getParameterValue();
    }
}
