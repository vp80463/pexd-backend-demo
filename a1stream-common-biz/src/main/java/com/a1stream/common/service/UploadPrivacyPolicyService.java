package com.a1stream.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.bo.FilesUploadUtilBO;
import com.a1stream.common.manager.FileUploadManager;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Service
public class UploadPrivacyPolicyService {

    @Resource
    private FileUploadManager fileUploadManager;

    public String privacyPolicyResultsFileUpload(String siteId, String mobilePhone, MultipartFile[] files) {

        FilesUploadUtilBO param = new FilesUploadUtilBO();

        param.setBusinessType("S074PRIVACYPOLICYRESULTS");
        param.setSiteId(siteId);
        param.setMultipleFiles(files);
        param.setBusinessRulesName(mobilePhone);

        Map<String, String> oldAndNewFileNameMap = fileUploadManager.multipleFileUpload(param);

        return oldAndNewFileNameMap.get(files[0].getOriginalFilename());
    }
}
