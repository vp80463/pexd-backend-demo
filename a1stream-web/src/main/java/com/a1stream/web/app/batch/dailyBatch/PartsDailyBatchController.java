package com.a1stream.web.app.batch.dailyBatch;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.listener.model.ThreadLocalPJAuditableDetailAccessor;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.bo.batch.ParameterBO;
import com.a1stream.parts.facade.PartsDailyBatchFacade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("public/dailyBatch")
public class PartsDailyBatchController implements RestProcessAware {

    public static final String JSON_EXTENTION = ".json";

    @Resource
    private PartsDailyBatchFacade partsDailyBatchFacade;

    @PostMapping(value = "/processStart" + JSON_EXTENTION)
    public BaseResult doPartsDailyBatchFacade(@RequestBody final ParameterBO model) {

        ThreadLocalPJAuditableDetailAccessor.getValue().setUpdateProgram("DailyBatch");
        ThreadLocalPJAuditableDetailAccessor.getValue().setBatchUser("DailyBatch");

        partsDailyBatchFacade.doPartsDailyBatchFacade(model);

        BaseResult result = new BaseResult();
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }
}

