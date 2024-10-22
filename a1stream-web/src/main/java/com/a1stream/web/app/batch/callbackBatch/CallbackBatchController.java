package com.a1stream.web.app.batch.callbackBatch;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.listener.model.ThreadLocalPJAuditableDetailAccessor;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.bo.batch.PartsDeadStockItemBO;
import com.a1stream.parts.facade.PartsDeadStockFacade;
import com.a1stream.service.facade.ServiceCallbackFacade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("public/callbackBatch")
public class CallbackBatchController implements RestProcessAware {

    public static final String JSON_EXTENTION = ".json";

    @Resource
    private PartsDeadStockFacade partsDeadStockFacade;
    @Resource
    private ServiceCallbackFacade serviceCallbackFacade;

    @PostMapping(value = "/deadstock" + JSON_EXTENTION)
    public BaseResult doDeadStockCalculate(@RequestBody final PartsDeadStockItemBO model) {

        ThreadLocalPJAuditableDetailAccessor.getValue().setUpdateProgram("CallbackBatch");
        ThreadLocalPJAuditableDetailAccessor.getValue().setBatchUser("CallbackBatch");

        partsDeadStockFacade.doDeadStockCalculate(model.getDealerCode());

        BaseResult result = new BaseResult();
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/generateSvRemind" + JSON_EXTENTION)
    public BaseResult doGenerateServiceRemind() {

        ThreadLocalPJAuditableDetailAccessor.getValue().setUpdateProgram("CallbackBatch");
        ThreadLocalPJAuditableDetailAccessor.getValue().setBatchUser("CallbackBatch");

        serviceCallbackFacade.generateServiceRemind();

        BaseResult result = new BaseResult();
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }
}

