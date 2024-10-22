package com.a1stream.web.app.batch.monthlyBatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.listener.model.ThreadLocalPJAuditableDetailAccessor;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.batch.DimMonthlyBatchBO;
import com.a1stream.ifs.facade.SpProductFacade;
import com.a1stream.parts.facade.PartsReturnRequestFacade;
import com.a1stream.parts.facade.PartsRopqBatchFacade;
import com.a1stream.parts.facade.PartsRopqDimBatchFacade;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("public/monthlyBatch")
public class PartsMonthlyBatchController implements RestProcessAware {

    public static final String JSON_EXTENTION = ".json";

    @Resource
    private PartsRopqBatchFacade partsBatchFacade;

    @Resource
    private SpProductFacade spProductFacade;

    @Resource
    private PartsRopqDimBatchFacade partsRopqDimBatchFacade;

    @Resource
    private PartsReturnRequestFacade partsReturnRequestFacade;

    @PostMapping(value = "/dimMonthly" + JSON_EXTENTION)
    public BaseResult partsDimMonthly(@RequestBody final DimMonthlyBatchBO model) {

        ThreadLocalPJAuditableDetailAccessor.getValue().setUpdateProgram("DimBatch");
        ThreadLocalPJAuditableDetailAccessor.getValue().setBatchUser("DimBatch");
        BaseResult result = new BaseResult();

        Assert.notNull(model, "Input arguments must be not be null!");

        List<Long> facilityList = new ArrayList<Long>();
        String siteFac = model.getSiteFac();
        String[] siteFacArry = siteFac.split(CommonConstants.CHAR_COLON);
        String siteId = siteFacArry[0];

        for (int i=1 ; i<siteFacArry.length ; i++) {
            facilityList.add(NumberUtil.toLong(siteFacArry[i]));
        }
        if (facilityList.size() > 0 && StringUtils.isNotBlankText(siteId)) {
            partsRopqDimBatchFacade.doPartsBatchFacade(siteId,facilityList);
        }

        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/ropqMonthly" + JSON_EXTENTION)
    public BaseResult partsMonthly(@RequestBody final DimMonthlyBatchBO model) {

        ThreadLocalPJAuditableDetailAccessor.getValue().setUpdateProgram("MonthlyBatch");
        ThreadLocalPJAuditableDetailAccessor.getValue().setBatchUser("MonthlyBatch");
        String groupNo =  model.getGroupNo();

        partsBatchFacade.doPartsBatchFacade(groupNo);

        BaseResult result = new BaseResult();
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }
}

