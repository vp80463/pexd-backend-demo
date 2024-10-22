package com.a1stream.web.app.controller.common;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.facade.UploadPrivacyPolicyFacade;
import com.a1stream.common.model.BaseResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("common/upload")
public class UploadPrivacyPolicyController implements RestProcessAware {

    @Resource
    private UploadPrivacyPolicyFacade facade;

    @PostMapping(value = "/privacyPolicyResultsFileUpload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult privacyPolicyResultsFileUpload(final HttpServletRequest request
                                                    , @RequestParam(value = "file") MultipartFile[] files
                                                    , @RequestParam(value = "mobilePhone", required = false) String mobilePhone
                                                    , @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        result.setData(facade.privacyPolicyResultsFileUpload(uc.getDealerCode(), mobilePhone, files));
        result.setMessage(BaseResult.SUCCESS_MESSAGE);

        return result;
    }
}