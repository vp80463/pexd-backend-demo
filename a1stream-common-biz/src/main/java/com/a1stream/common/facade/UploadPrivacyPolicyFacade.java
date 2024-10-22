package com.a1stream.common.facade;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.service.UploadPrivacyPolicyService;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
 * @author dong zhen
 */
@Component
public class UploadPrivacyPolicyFacade {

    @Resource
    private UploadPrivacyPolicyService service;

    public String privacyPolicyResultsFileUpload(String siteId, String mobilePhone, MultipartFile[] files) {

        if (files != null && files.length > 0 && !StringUtils.isEmpty(mobilePhone)){

            return service.privacyPolicyResultsFileUpload(siteId, mobilePhone, files);
        } else {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10401"));
        }
    }
}
