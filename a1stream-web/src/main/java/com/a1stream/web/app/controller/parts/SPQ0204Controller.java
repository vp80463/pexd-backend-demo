/**
 *
 */
package com.a1stream.web.app.controller.parts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.bo.parts.SPQ020401BO;
import com.a1stream.domain.form.parts.SPQ020401Form;
import com.a1stream.parts.facade.SPQ0204Facade;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("parts/spq0201")
public class SPQ0204Controller implements RestProcessAware{

    @Resource
    private SPQ0204Facade spq0204Facade;

    @PostMapping(value = "/searchInvoiceInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPQ020401BO searchInvoiceInfo(@RequestBody final SPQ020401Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spq0204Facade.searchInvoiceInfo(form);
    }


    @PostMapping(value = "/confirmInvoiceInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmInvoiceInfo(@RequestBody final SPQ020401Form form,@AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        spq0204Facade.confirmInvoiceInfo(form);
    }

    @PostMapping(value = "/exportPurchaseOrderExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportPartsStockExcel(HttpServletResponse response,@RequestBody final SPQ020401Form form, @AuthenticationPrincipal final PJUserDetails uc) throws IOException {

        form.setSiteId(uc.getDealerCode());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("example.xlsx", StandardCharsets.UTF_8));

        SPQ020401BO result = spq0204Facade.searchInvoiceInfoExport(form);
        var summaryContent = result.getSummaryContent();
        var detailContent = result.getDetailContent();

        //多sheet导出
        try (InputStream templateInputStream = getClass().getResourceAsStream(FileConstants.EXCEL_EXPORT_SPQ0204_01);
             OutputStream outputStream = response.getOutputStream()) {
            ExcelWriter excelWriter = EasyExcelFactory.write(outputStream)
                    .withTemplate(templateInputStream)
                    .relativeHeadRowIndex(0)
                    .needHead(false)
                    .build();
            WriteSheet sheet1 = EasyExcelFactory.writerSheet(CommonConstants.CHAR_SHEET_SUMMARY).build();
            excelWriter.write(summaryContent, sheet1);
            WriteSheet sheet2 = EasyExcelFactory.writerSheet(CommonConstants.CHAR_SHEET_DETAIL).build();
            excelWriter.write(detailContent, sheet2);
            excelWriter.finish();
        } catch (Exception e) {
            throw new PJCustomException(BaseResult.ERROR_MESSAGE);
        }
    }
}
