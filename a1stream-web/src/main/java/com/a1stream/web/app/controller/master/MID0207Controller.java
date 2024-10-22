package com.a1stream.web.app.controller.master;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.form.master.MID0207Form;
import com.a1stream.master.facade.MID0207Facade;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("master/mid0207")
public class MID0207Controller implements RestProcessAware{

    @Resource
    private MID0207Facade mid0207Facade;

    private ExcelFileExporter exporter;

    @Autowired
    public void setExporter(ExcelFileExporter exporter) {
        this.exporter = exporter;
    }

    @PostMapping(value = "/checkFile.json")
    public MID0207Form checkFile(@RequestBody final MID0207Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        mid0207Facade.checkFile(form);
        return form;
    }

    @PostMapping(value = "/importFile.json")
    public MID0207Form importFile(@RequestBody final MID0207Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        mid0207Facade.importFile(form);
        return form;
    }

    @PostMapping(value = "/validFileDownload.json")
    public DownloadResponseView validFileDownload(@RequestBody final MID0207Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = mid0207Facade.getValidFileList(form);
        DownloadResponse templateExcel = exporter.generate("cmm010102_template.xlsx", exportList, "cmm010102_template.xlsx");

        return new DownloadResponseView(templateExcel);
    }

    @PostMapping(value = "/templateDownload.json")
    public DownloadResponseView downloadExcelFile(@RequestBody final MID0207Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = mid0207Facade.getExportList(form);
        DownloadResponse templateExcel = exporter.generate("cmm010102_template.xlsx", exportList, "cmm010102_template.xlsx");

        return new DownloadResponseView(templateExcel);
    }

    @PostMapping(value = "/templateDownload2.json", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadExcel(HttpServletResponse response, @RequestBody final MID0207Form form, @AuthenticationPrincipal final PJUserDetails uc) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("example.xlsx", StandardCharsets.UTF_8));

        var exportList1 = mid0207Facade.getExportList2(form);
        var exportList2 = mid0207Facade.getExportList3(form);

        //单sheet导出
//        try (InputStream templateInputStream = getClass().getResourceAsStream("/reportTemplate/cmm010101_template.xlsx");
//             OutputStream outputStream = response.getOutputStream()) {
//            EasyExcel.write(outputStream)
//                    .withTemplate(templateInputStream)
//                    .relativeHeadRowIndex(0)
//                    .sheet("Sheet1")
//                    .doWrite(exportList);
//         } catch (Exception e) {
//            throw new PJCustomException(BaseResult.ERROR_MESSAGE);
//        }

        //多sheet导出
        try (InputStream templateInputStream = getClass().getResourceAsStream("/reportTemplate/cmm010101_template.xlsx");
             OutputStream outputStream = response.getOutputStream()) {
            ExcelWriter excelWriter = EasyExcelFactory.write(outputStream)
                    .withTemplate(templateInputStream)
                    .relativeHeadRowIndex(0)
                    .needHead(false)
                    .build();

            WriteSheet sheet1 = EasyExcelFactory.writerSheet("Sheet1").build();
            excelWriter.write(exportList1, sheet1);

            WriteSheet sheet2 = EasyExcelFactory.writerSheet("Sheet2").build();
            excelWriter.write(exportList2, sheet2);

            excelWriter.finish();
        } catch (Exception e) {
            throw new PJCustomException(BaseResult.ERROR_MESSAGE);
        }
    }
}