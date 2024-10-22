package com.a1stream.web.app.controller.parts;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ030301BO;
import com.a1stream.domain.bo.parts.SPQ030302BO;
import com.a1stream.domain.form.parts.SPQ030301Form;
import com.a1stream.domain.form.parts.SPQ030302Form;
import com.a1stream.parts.facade.SPQ0303Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 * 
* 功能描述:Parts Stock Information Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@RestController
@RequestMapping("parts/spq0303")
public class SPQ0303Controller implements RestProcessAware {

    @Resource
    private SPQ0303Facade spq0303Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getPartsStockList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ030301BO> getPartsStockListPageable(@RequestBody final SPQ030301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0303Facade.getPartsStockListPageable(form, uc.getDealerCode());
    }

    @PostMapping(value = "/exportPartsStockExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockExcel(@RequestBody final SPQ030301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setPageFlg(false);
        Page<SPQ030301BO> datas = spq0303Facade.getPartsStockListPageable(form, uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0303_01, datas, FileConstants.EXCEL_EXPORT_SPQ0303_01);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/getPartsStockDetailList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ030302BO> getPartsStockDetailListPageable(@RequestBody final SPQ030302Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0303Facade.getPartsStockDetailListPageable(form, uc.getDealerCode());
    }

    @PostMapping(value = "/exportPartsStockDetailExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockDetailExcel(@RequestBody final SPQ030302Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setPageFlg(false);
        Page<SPQ030302BO> datas = spq0303Facade.getPartsStockDetailListPageable(form, uc.getDealerCode());
        for (SPQ030302BO bo : datas) {
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
            bo.setSupersedingParts(PartNoUtil.format(bo.getSupersedingParts()));
        }
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0303_02, datas, FileConstants.EXCEL_EXPORT_SPQ0303_02);
        return new DownloadResponseView(excel);
    }

}
