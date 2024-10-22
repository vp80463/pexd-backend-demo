package com.a1stream.web.app.controller.test;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.form.master.FileUploadTestForm;
import com.a1stream.unit.facade.FileUploadTestFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("public/test")
public class FileUploadTestController implements RestProcessAware{

    @Resource
    private FileUploadTestFacade fileUploadTestFacade;

    @PostMapping(value = "/multipleFileUploadTest.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult multipleFileUploadTest(final HttpServletRequest request
            , @RequestPart(value = "uploadFiles") MultipartFile[] files
            , @RequestParam(value = "formData") String formDataJson
            , @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知属性 加上这个方法可以让formDataJson里面存在的字段和FileUploadTestForm里面的字段不用一一对应，如果不加就会强行校验对应
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FileUploadTestForm form;
        try {
            form = objectMapper.readValue(formDataJson, FileUploadTestForm.class);
        } catch (JsonProcessingException e) {
            throw new PJCustomException("解析formData失败");
        }
        form.setFiles(files);
        fileUploadTestFacade.multipleFileUploadTest(form);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/singleFileUploadTest.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult singleFileUploadTest(final HttpServletRequest request
            , @RequestPart(value = "uploadFiles") MultipartFile[] singleFile
            , @RequestParam(value = "formData") String formDataJson
            , @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FileUploadTestForm form;
        try {
            form = objectMapper.readValue(formDataJson, FileUploadTestForm.class);
        } catch (JsonProcessingException e) {
            throw new PJCustomException("解析formData失败");
        }
        fileUploadTestFacade.singleFileUploadTest(form, singleFile);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/escortFileUploadTest.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult escortFileUploadTest(final HttpServletRequest request
            , @RequestParam(value = "file") MultipartFile[] files
            , @RequestParam(value = "formData", required = false) String formDataJson
            , @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FileUploadTestForm form = new FileUploadTestForm();
        if (StringUtils.isNotEmpty(formDataJson)){
            try {
                form = objectMapper.readValue(formDataJson, FileUploadTestForm.class);
            } catch (JsonProcessingException e) {
                throw new PJCustomException("解析formData失败");
            }
        }
        fileUploadTestFacade.escortFileUploadTest(form, files);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/listImageUrl.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult listImageUrl(@RequestBody final FileUploadTestForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        result.setData(fileUploadTestFacade.getImageUrlList(form));
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/SDM050101Test.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult SDM050101Test(final HttpServletRequest request
            , @RequestParam(value = "file") MultipartFile file
            , @RequestParam(value = "formData", required = false) String formDataJson
            , @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FileUploadTestForm form = new FileUploadTestForm();
        if (StringUtils.isNotEmpty(formDataJson)){
            try {
                form = objectMapper.readValue(formDataJson, FileUploadTestForm.class);
            } catch (JsonProcessingException e) {
                throw new PJCustomException("解析formData失败");
            }
        }
        fileUploadTestFacade.SDM050101Test(form, file, result);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }
}