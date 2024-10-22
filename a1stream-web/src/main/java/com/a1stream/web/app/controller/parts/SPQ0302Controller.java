package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ030201BO;
import com.a1stream.domain.form.parts.SPQ030201Form;
import com.a1stream.parts.facade.SPQ0302Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@RestController
@RequestMapping("parts/spq0302")
public class SPQ0302Controller implements RestProcessAware{

    @Resource
    private SPQ0302Facade spq0302Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findPartsAdjustmentHistoryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ030201BO> findPartsAdjustmentHistoryList(@RequestBody final SPQ030201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return spq0302Facade.findPartsAdjustmentHistoryList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/exportPartsAdjustmentHistory.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsAdjustmentHistory(@RequestBody final SPQ030201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0302_01, spq0302Facade.exportPartsAdjustmentHistory(model, uc.getDealerCode()), FileConstants.EXCEL_EXPORT_SPQ0302_01);
        return new DownloadResponseView(excel);
    }
}