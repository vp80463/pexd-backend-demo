package com.a1stream.web.app.ifs.consumer.async.listen;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.ifs.Interf;
import com.a1stream.common.ifs.InterfProcessRequest;
import com.a1stream.common.ifs.InterfProcessResponse;
import com.a1stream.common.ifs.InterfProcessor;
import com.a1stream.unit.facade.AbcFacade;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Interf(code = InterfCode.A1STREAM_YMVNDMS_ASYNCSQSTEST)
public class IfsAsyncTestController implements InterfProcessor<InterfProcessRequest, Void>{

    @Resource
    private AbcFacade abcFacade;

    @Override
    public InterfProcessResponse execute(InterfProcessRequest request) {

        String code = "SUCCESS";
        String message = "excute message";
        InterfProcessResponse response = new InterfProcessResponse();

        //获取上游系统发送过来的消息体
        //List<DispatchApiModel> modelList = JSONArray.parseArray(JSON.toJSONString(requestModel.getBody()), DispatchApiModel.class);

        //这个干嘛的需要问一下
        //List<BaseExceptionModel> errorList;
        try {
            //执行的具体逻辑
            System.out.println("excute new ifs async");
        } catch (Exception e) {
            log.error("InterfProcessResponse:", e);
            response.setCode("FAILURE");
            response.setMessage(e.getMessage());
            response.setData("");
        }

        return response;
    }
}
