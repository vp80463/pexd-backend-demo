package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.master.CMM060101BO;
import com.a1stream.domain.form.master.CMM060101Form;
import com.a1stream.master.facade.CMM0601Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Service Job Entry
*
* @author mid1966
*/
@RestController
@RequestMapping("master/cmm0601")
public class CMM0601Controller implements RestProcessAware {

    @Resource
    private CMM0601Facade cmm0601Fac;

    @Resource
    private ExcelFileExporter exporter;

    @PostMapping(value = "/getSvJobData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM060101BO> getSvJobData(@RequestBody final CMM060101Form model) {

        return cmm0601Fac.getSvJobData(model);
    }

    @PostMapping(value = "/exportSvJobData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportSvJobData(@RequestBody final CMM060101Form model) {

        List<CMM060101BO> datas = cmm0601Fac.getSvJobData(model);
        DownloadResponse excel = exporter.generate(FileConstants.EXCEL_TEMPLATE_CMM0601_01, datas, FileConstants.EXCEL_EXPORT_CMM0601_01);

        return new DownloadResponseView(excel);
    }
}