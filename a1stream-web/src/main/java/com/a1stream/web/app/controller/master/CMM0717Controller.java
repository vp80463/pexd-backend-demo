package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.master.CMM071701BO;
import com.a1stream.domain.form.master.CMM071701Form;
import com.a1stream.master.facade.CMM0717Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@RestController
@RequestMapping("master/cmm0717")
@FunctionId("CMM0717")
public class CMM0717Controller implements RestProcessAware {

    @Resource
    private CMM0717Facade cmm0717Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/retrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM071701BO> findRetrieveList(@RequestBody final CMM071701Form form) {

        return cmm0717Facade.findRetrieveList(form);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload(@RequestBody final CMM071701Form form) {

        List<CMM071701BO> datas = form.getGridDataList();
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_CMM0717_01, datas, FileConstants.EXCEL_EXPORT_CMM0717_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM071701Form checkFile(@RequestBody final CMM071701Form form) {

        return cmm0717Facade.checkFile(form);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final CMM071701Form form) {

        cmm0717Facade.confirm(form);
    }
}