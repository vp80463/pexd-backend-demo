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

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.master.CMQ050801BO;
import com.a1stream.domain.form.master.CMQ050801Form;
import com.a1stream.master.facade.CMQ0508Facade;
import com.alibaba.excel.EasyExcel;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

/**
* 功能描述:Parts Summary Information明细画面
*
* mid2330
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Liu Chaoran     New
*/
@RestController
@RequestMapping("master/cmq0508")
public class CMQ0508Controller implements RestProcessAware{

    @Resource
    private CMQ0508Facade cmq0508Facade;

    @PostMapping(value = "/findPartsSummaryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CMQ050801BO> findPartsSummaryList(@RequestBody final CMQ050801Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmq0508Facade.findPartsSummaryList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/exprotPartsSummaryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void downloadExcel(HttpServletResponse response, @RequestBody final CMQ050801Form form, @AuthenticationPrincipal final PJUserDetails uc) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(FileConstants.EXCEL_EXPORT_CMQ0508_01, StandardCharsets.UTF_8));
        var exportList = cmq0508Facade.findPartsSummaryExportList(form, uc.getDealerCode());
        try (InputStream templateInputStream = getClass().getResourceAsStream(FileConstants.EXCEL_TEMPLATE_CMQ0508_01);
                OutputStream outputStream = response.getOutputStream()) {
            EasyExcel.write(outputStream)
                    .withTemplate(templateInputStream)
                    .relativeHeadRowIndex(0)
                    .sheet("Sheet1")
                    .doWrite(exportList);
        }
    }
}