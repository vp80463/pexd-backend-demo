package com.a1stream.unit.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.bo.FilesUploadReturnBO;
import com.a1stream.common.bo.FilesUploadUtilBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.manager.BaseTnvoiceManager;
import com.a1stream.common.manager.FileUploadManager;
import com.a1stream.common.model.EInvoiceBO;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.form.master.FileUploadTestForm;
import com.a1stream.domain.repository.CmmUploadFileRepository;
import com.a1stream.domain.repository.SystemParameterRepository;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Service
public class FileUploadTestService {

    @Resource
    private FileUploadManager fileUploadManager;

    @Resource
    private CmmUploadFileRepository cmmUploadFileRepository;

    @Resource
    private BaseTnvoiceManager baseTnvoiceManager;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    public void multipleFileUploadTest(FileUploadTestForm form, Long businessId) {

        System.out.println("业务逻辑");

        FilesUploadUtilBO fileUploadUtil = new FilesUploadUtilBO();
        fileUploadUtil.setBusinessId(businessId);
        fileUploadUtil.setBusinessType(form.getBusinessType());
        fileUploadUtil.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        fileUploadUtil.setMultipleFiles(form.getFiles());
        fileUploadUtil.setBusinessRulesName("132996677556_order");
        fileUploadManager.multipleFileUpload(fileUploadUtil);
    }

    public void singleFileUploadTest(FileUploadTestForm form, MultipartFile[] singleFile, Long businessId) {

        System.out.println("业务逻辑2");

        FilesUploadUtilBO fileUploadUtil = new FilesUploadUtilBO();
        fileUploadUtil.setBusinessId(businessId);
        fileUploadUtil.setBusinessType(form.getBusinessType());
        fileUploadUtil.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        fileUploadUtil.setMultipleFiles(singleFile);
        fileUploadManager.multipleFileUpload(fileUploadUtil);
    }

    public void escortFileUploadTest(FileUploadTestForm form, MultipartFile[] files, Long businessId) {

        System.out.println("业务逻辑3");

        FilesUploadUtilBO fileUploadUtil = new FilesUploadUtilBO();
        fileUploadUtil.setBusinessId(businessId);
        fileUploadUtil.setBusinessType(form.getBusinessType());
        fileUploadUtil.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        fileUploadUtil.setMultipleFiles(files);
        fileUploadUtil.setBusinessRulesName("AAA_FILES_18663322452");
        fileUploadManager.multipleFileUpload(fileUploadUtil);
    }

    public List<FilesUploadReturnBO> getImageUrlList(FileUploadTestForm form) {

        System.out.println("业务逻辑4");
        return fileUploadManager.getUrlListByBusinessType(form.getBusinessType());
    }

    public void testWsdl(EInvoiceBO invoiceBO) {

        String type = "C165EINVOICEMCRETAIL";
        String area = "North";
        baseTnvoiceManager.getEInvoiceResult(invoiceBO, type, area);
    }
    
    public FilesUploadUtilBO SDM050101Test(FileUploadTestForm form, MultipartFile file, Long businessId) {

        FilesUploadUtilBO fileUploadUtil = new FilesUploadUtilBO();
        fileUploadUtil.setBusinessId(businessId);
        fileUploadUtil.setBusinessType(form.getBusinessType());
        fileUploadUtil.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        fileUploadUtil.setSingleFile(file);
        fileUploadUtil.setBusinessRulesName("SDM050501");
        Map<String, String> oldAndNewFileNameMap = fileUploadManager.singleFileUpload(fileUploadUtil);
        fileUploadUtil.setOldAndNewFileNameMap(oldAndNewFileNameMap);
        return fileUploadUtil;
    }

    public String getPath(String systemParameterTypeId) {

        SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(systemParameterTypeId);
        return systemParameter.getParameterValue();
    }
}
