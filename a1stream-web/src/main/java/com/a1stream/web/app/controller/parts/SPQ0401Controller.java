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
import com.a1stream.domain.bo.parts.SPQ040101BO;
import com.a1stream.domain.form.parts.SPQ040101Form;
import com.a1stream.parts.facade.SPQ0401Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("parts/spq0401")
public class SPQ0401Controller implements RestProcessAware{

    @Resource
    private SPQ0401Facade spq0401Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/searchPurchaseOrderList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ040101BO> searchPurchaseOrderList(@RequestBody final SPQ040101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return spq0401Facade.searchPurchaseOrderList(form, uc.getDealerCode());
    }

    @PostMapping(value = "/exportPurchaseOrderExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockExcel(@RequestBody final SPQ040101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setPageFlg(false);
        Page<SPQ040101BO> datas = spq0401Facade.searchPurchaseOrderList(form, uc.getDealerCode());
        for (SPQ040101BO bo: datas) {
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
        }
        DownloadResponse excel =excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0401_01
                                                         , datas
                                                         , FileConstants.EXCEL_TEMPLATE_SPQ0401_01);

        return new DownloadResponseView(excel);
    }
}