package com.a1stream.web.app.ifs.consumer.sync;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.ifs.facade.IfsSdToDmsFacade;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("public/a1stream")
@RestController
@FunctionId("IFS")
public class IfsSdToDmsController {

    @Resource
    private IfsSdToDmsFacade sdToDmsFac;

    private static final String SUCCESS_CODE = "SUCCESS";
    private static final String FAILURE_CODE = "FAILURE";
    private static final String DETAIL = "detail";
    private static final String ERROR_LOG = "InterfProcessResponse";

    //SdToDms_SdProductInfo
    @PostMapping(value = "/sdProductInfo.json")
    public BaseInfResponse sdProductInfo(@RequestBody String jsonString) {

        return execute(InterfCode.IMPORTTODMS_SDPRODUCTINFO, jsonString);
    }

    //SdToDms_SdManifest
    @PostMapping(value = "/sdManifest.json")
    public BaseInfResponse sdManifest(@RequestBody String jsonString) {

        return execute(InterfCode.SD_TO_DMS_SDMANIFEST, jsonString);
    }

    public BaseInfResponse execute(String interfCd, String jsonString) {

        BaseInfResponse response = new BaseInfResponse();
        response.setCode(SUCCESS_CODE);
        response.setMessage(CommonConstants.CHAR_BLANK);
        response.setData(CommonConstants.CHAR_BLANK);
        try {
            //获取上游系统发送过来的消息体
            JSONObject json = JSONObject.parseObject(jsonString);
            String detail = JSONArray.toJSONString(json.get(DETAIL));
            if(!StringUtils.isEmpty(detail)) {
                sdToDmsFac.doBusinessProcess(interfCd, detail);
            }
        } catch (Exception e) {
            log.error(ERROR_LOG, e);
            response.setCode(FAILURE_CODE);
            response.setMessage(e.getMessage());
            response.setData(e.getMessage());
        }

        return response;
    }

}
