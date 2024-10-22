package com.a1stream.web.app.batch.callbackBatch;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.listener.model.ThreadLocalPJAuditableDetailAccessor;
import com.a1stream.common.model.BaseResult;
import com.a1stream.unit.facade.SdPSIExportFacade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("public/callbackBatch")
public class SdPSIExportController implements RestProcessAware {

    @Resource
    private SdPSIExportFacade sdPSIExportFacade;

    public static final String JSON_EXTENTION = ".json";

    @PostMapping(value = "/sdPSIExport" + JSON_EXTENTION)
    public BaseResult doSdPSIExport() {

        ThreadLocalPJAuditableDetailAccessor.getValue().setUpdateProgram("CallbackBatch");
        ThreadLocalPJAuditableDetailAccessor.getValue().setBatchUser("CallbackBatch");

        sdPSIExportFacade.doSdPSIExport();

        BaseResult result = new BaseResult();
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

}
