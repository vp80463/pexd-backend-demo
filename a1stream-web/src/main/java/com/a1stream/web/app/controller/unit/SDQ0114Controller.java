package com.a1stream.web.app.controller.unit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDQ011401BO;
import com.a1stream.domain.bo.unit.SDQ011402BO;
import com.a1stream.domain.form.unit.SDQ011401Form;
import com.a1stream.domain.form.unit.SDQ011402Form;
import com.a1stream.unit.facade.SDQ0114Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Stock In Out History Inquiry
*
* mid2287
* 2024年7月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/25   Wang Nan      New
*/
@RestController
@RequestMapping("unit/sdq0114")
public class SDQ0114Controller implements RestProcessAware {

    @Resource
    private SDQ0114Facade sdq0114Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getStockInOutHistoryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SDQ011401BO> getStockInOutHistoryList(@RequestBody final SDQ011401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return sdq0114Facade.getStockInOutHistoryList(form);
    }

    @PostMapping(value = "/exportStockInOutHistoryExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportStockInOutHistoryExcel(@RequestBody final SDQ011401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setPageFlg(false);
        form.setSiteId(uc.getDealerCode());
        Page<SDQ011401BO> data = sdq0114Facade.getStockInOutHistoryList(form);

        //导出日期格式化
        for (SDQ011401BO bo : data) {

            if (StringUtils.isNotBlank(bo.getTransactionDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
                bo.setTransactionDate(LocalDate.parse(bo.getTransactionDate(), formatter)
                                               .format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            }
        }

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDQ0114_01, data, FileConstants.EXCEL_EXPORT_SDQ0114_01);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/getStockHistoryDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDQ011402BO> getStockHistoryDetail(@RequestBody final SDQ011402Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return sdq0114Facade.getStockHistoryDetail(form);
    }
}
