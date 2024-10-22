package com.a1stream.web.app.ifs.consumer.async.listen;

import java.util.Objects;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.ifs.Interf;
import com.a1stream.common.ifs.InterfProcessRequest;
import com.a1stream.common.ifs.InterfProcessResponse;
import com.a1stream.common.ifs.InterfProcessor;
import com.a1stream.service.facade.SVM0102Facade;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Interf(code = InterfCode.DMS_TO_DMS_SV_SERVICESETTLE)
public class IfsServiceSettleController implements InterfProcessor<InterfProcessRequest, Void>{

    @Resource
    private SVM0102Facade svm0102Facade;

    @Override
    public InterfProcessResponse execute(InterfProcessRequest request) {

        InterfProcessResponse response = new InterfProcessResponse();

        //serviceOrderId不存在
        if (Objects.isNull(request.getBody())) {response.setCode("FAILURE"); response.setMessage("Service Order ID is not exists!"); return response;}

        try {
            svm0102Facade.asynOperationAfterSettle((Long)request.getBody());
        } catch (Exception e) {
            log.error("InterfProcessResponse:", e);
            response.setCode("FAILURE");
            response.setMessage(e.getMessage());
            response.setData("");
        }

        return response;
    }
}
