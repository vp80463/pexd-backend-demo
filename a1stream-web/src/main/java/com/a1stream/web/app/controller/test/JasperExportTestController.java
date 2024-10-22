package com.a1stream.web.app.controller.test;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.JasperExportTestBO;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.form.master.FileUploadTestForm;
import com.a1stream.unit.facade.FileUploadTestFacade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("public/test")
public class JasperExportTestController implements RestProcessAware{

    @Resource
    private FileUploadTestFacade fileUploadTestFacade;

    private PdfReportExporter pdfExporter;

    @Autowired
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    @PostMapping(value = "/jasperExportExample.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView jasperExportExample(@RequestBody final FileUploadTestForm form
            , @AuthenticationPrincipal final PJUserDetails uc) {

        List<JasperExportTestBO> dataList = fileUploadTestFacade.getJasperDataList(form);

        DownloadResponse downloadResponse = pdfExporter.generate("jasperTest", dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

    @PostMapping(value = "/jasperExportExample2.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView jasperExportExample2(@RequestBody final FileUploadTestForm form
            , @AuthenticationPrincipal final PJUserDetails uc) {

        List<JasperExportTestBO> dataList = fileUploadTestFacade.getJasperDataList(form);

        DownloadResponse downloadResponse = pdfExporter.generate("jasperTest2", dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

    @PostMapping(value = "/wsdlTest.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult wsdlTest(@RequestBody final FileUploadTestForm form
            , @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        fileUploadTestFacade.testWsdl();
        return result;
    }

    @PostMapping(value = "/shellTest.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void shellTest() {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("当前时间: " + now.format(formatter));
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");

    }
}