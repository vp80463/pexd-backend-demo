package com.a1stream.web.app.controller.master;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.master.CMM050101BO;
import com.a1stream.domain.bo.master.CMM050102BO;
import com.a1stream.domain.form.master.CMM050101Form;
import com.a1stream.domain.form.master.CMM050102Form;
import com.a1stream.master.facade.CMM0501Facade;
import com.alibaba.excel.EasyExcel;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author liu chaoran
 */
@RestController
@RequestMapping("master/cmm0501")
@FunctionId("CMM0501")
public class CMM0501Controller implements RestProcessAware{

    @Resource
    private CMM0501Facade cmm0501Facade;

    @PostMapping(value = "/findPartsInformationInquiryAndPageList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CMM050101BO> findPartsInformationInquiryAndPageList(@RequestBody final CMM050101Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0501Facade.findPartsInformationInquiryAndPageList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/exportPartsInformation.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void downloadExcel(HttpServletResponse response, @RequestBody final CMM050101Form form, @AuthenticationPrincipal final PJUserDetails uc) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(FileConstants.EXCEL_EXPORT_CMM0501_01, StandardCharsets.UTF_8));
        var exportList = cmm0501Facade.findPartsInformationInquiryList(form, uc.getDealerCode());
        try (InputStream templateInputStream = getClass().getResourceAsStream(FileConstants.EXCEL_TEMPLATE_CMM0501_01);
                OutputStream outputStream = response.getOutputStream()) {
            EasyExcel.write(outputStream)
                    .withTemplate(templateInputStream)
                    .relativeHeadRowIndex(0)
                    .sheet("Sheet1")
                    .doWrite(exportList);
        }
    }

    @PostMapping(value = "/getBasicInfoList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM050102BO getBasicInfoList(@RequestBody final CMM050102Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0501Facade.getBasicInfoList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/confirmPartsInformationMaintenance.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmPartsInformationMaintenance(@RequestBody final CMM050102Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setPartsCd(PartNoUtil.formaForDB(model.getPartsCd()));
        cmm0501Facade.confirmPartsInformationMaintenance(model, uc);
    }

}